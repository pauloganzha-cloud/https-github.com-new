/*
 * LG3D Native X11 Integration
 *
 * Original code from Project Looking Glass
 * Modernized for x86_64 and modern X11
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/keysym.h>
#include <X11/extensions/Xdamage.h>
#include <X11/extensions/Xcomposite.h>

#include "lg3d_x11.h"

#define MAX_WINDOWS 256

typedef struct {
    Window window;
    Display *display;
    int width;
    int height;
    unsigned char *pixels;
    int needs_update;
} lg3d_window_t;

static lg3d_window_t windows[MAX_WINDOWS];
static int window_count = 0;

/*
 * Initialize native X11 support
 */
int lg3d_x11_init(const char *display_name) {
    Display *display = XOpenDisplay(display_name);
    if (!display) {
        fprintf(stderr, "Failed to open display: %s\n", display_name);
        return -1;
    }

    // Initialize damage extension
    int damage_event, damage_error;
    if (!XDamageQueryExtension(display, &damage_event, &damage_error)) {
        fprintf(stderr, "XDamage extension not available\n");
    }

    // Initialize composite extension
    int major, minor;
    if (!XCompositeQueryVersion(display, &major, &minor)) {
        fprintf(stderr, "XComposite extension not available\n");
    }

    printf("X11 initialized: version %d.%d\n", major, minor);
    return 0;
}

/*
 * Create a new X11 window
 */
long lg3d_x11_create_window(Display *display, Window parent,
                            int x, int y, int width, int height) {

    if (window_count >= MAX_WINDOWS) {
        fprintf(stderr, "Maximum window count reached\n");
        return 0;
    }

    Window window = XCreateSimpleWindow(
        display, parent,
        x, y, width, height,
        0,
        BlackPixel(display, DefaultScreen(display)),
        BlackPixel(display, DefaultScreen(display))
    );

    if (!window) {
        fprintf(stderr, "Failed to create window\n");
        return 0;
    }

    // Select input events
    XSelectInput(display, window,
        ExposureMask | KeyPressMask | KeyReleaseMask |
        ButtonPressMask | ButtonReleaseMask | PointerMotionMask |
        StructureNotifyMask | FocusChangeMask);

    // Enable damage tracking for efficient updates
    // XDamageCreate(display, window, XDamageReportRawRectangles);

    windows[window_count].window = window;
    windows[window_count].display = display;
    windows[window_count].width = width;
    windows[window_count].height = height;
    windows[window_count].pixels = malloc(width * height * 4);
    windows[window_count].needs_update = 1;
    window_count++;

    printf("Created window: %lu\n", window);
    return window;
}

/*
 * Destroy an X11 window
 */
void lg3d_x11_destroy_window(Display *display, Window window) {
    for (int i = 0; i < window_count; i++) {
        if (windows[i].window == window) {
            XDestroyWindow(display, window);
            free(windows[i].pixels);

            // Remove from array (shift elements)
            for (int j = i; j < window_count - 1; j++) {
                windows[j] = windows[j + 1];
            }
            window_count--;
            break;
        }
    }
}

/*
 * Get window pixel data for texture
 */
int lg3d_x11_get_window_pixels(Display *display, Window window,
                               unsigned char *pixels, int *width, int *height) {

    // Find window in array
    for (int i = 0; i < window_count; i++) {
        if (windows[i].window == window) {
            if (windows[i].needs_update) {
                // In a real implementation, use XGetImage:
                // XImage *img = XGetImage(display, window, 0, 0,
                //                         w, h, AllPlanes, ZPixmap);

                // For now, just copy placeholder
                memset(windows[i].pixels, 128, windows[i].width * windows[i].height * 4);
                windows[i].needs_update = 0;
            }

            memcpy(pixels, windows[i].pixels, windows[i].width * windows[i].height * 4);
            *width = windows[i].width;
            *height = windows[i].height;
            return 0;
        }
    }

    return -1;
}

/*
 * Process X11 events
 */
int lg3d_x11_process_event(Display *display, XEvent *event) {
    switch (event->type) {
        case Expose:
            printf("Expose event on window: %lu\n", event->xexpose.window);
            // Mark window for texture update
            for (int i = 0; i < window_count; i++) {
                if (windows[i].window == event->xexpose.window) {
                    windows[i].needs_update = 1;
                }
            }
            return 1;

        case ConfigureNotify:
            printf("Configure event: %dx%d\n",
                   event->xconfigure.width, event->xconfigure.height);
            return 2;

        case KeyPress:
            printf("Key press: %d\n", event->xkey.keycode);
            return 3;

        case KeyRelease:
            return 4;

        case ButtonPress:
            printf("Button press: %d at %d,%d\n",
                   event->xbutton.button, event->xbutton.x, event->xbutton.y);
            return 5;

        case MotionNotify:
            return 6;

        case FocusIn:
        case FocusOut:
            return 7;

        default:
            return 0;
    }
}

/*
 * Cleanup native X11
 */
void lg3d_x11_cleanup(Display *display) {
    for (int i = 0; i < window_count; i++) {
        if (windows[i].pixels) {
            free(windows[i].pixels);
        }
    }
    window_count = 0;
}

/*
 * JNI wrappers
 */
JNIEXPORT jlong JNICALL Java_org_jdesktop_lg3d_displayserver_x11_NativeX11_XOpenDisplay
  (JNIEnv *env, jobject obj, jstring jdisplay) {
    const char *display = jdisplay ? (*env)->GetStringUTFChars(env, jdisplay, NULL) : NULL;
    Display *dpy = XOpenDisplay(display);
    if (jdisplay) (*env)->ReleaseStringUTFChars(env, jdisplay, display);
    return (jlong)(long)dpy;
}

JNIEXPORT jint JNICALL Java_org_jdesktop_lg3d_displayserver_x11_NativeX11_XCloseDisplay
  (JNIEnv *env, jobject obj, jlong jdisplay) {
    return XCloseDisplay((Display*)(long)jdisplay);
}