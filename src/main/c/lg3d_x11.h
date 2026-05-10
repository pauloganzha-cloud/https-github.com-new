/*
 * LG3D X11 Native Header
 */

#ifndef LG3D_X11_H
#define LG3D_X11_H

#include <jni.h>

/* Initialization */
int lg3d_x11_init(const char *display_name);

/* Window management */
long lg3d_x11_create_window(void *display, long parent, int x, int y, int w, int h);
void lg3d_x11_destroy_window(void *display, long window);
int lg3d_x11_get_window_pixels(void *display, long window, unsigned char *pixels, int *w, int *h);

/* Event processing */
int lg3d_x11_process_event(void *display, void *event);

/* Cleanup */
void lg3d_x11_cleanup(void *display);

#endif /* LG3D_X11_H */