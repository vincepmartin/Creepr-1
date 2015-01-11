#include <pebble.h>

static Window *s_main_window;
static TextLayer *s_output_layer;

static void tap_handler(AccelAxisType axis, int32_t direction) {
  static char s_buffer[128];
  static int tap_number = 0;

  snprintf(s_buffer, sizeof(s_buffer),
    "%d", ++tap_number
  );

  //Show the data
  text_layer_set_text(s_output_layer, s_buffer);
  vibes_short_pulse();
//   switch (axis) {
//   case ACCEL_AXIS_X:
//     if (direction > 0) {
//       text_layer_set_text(s_output_layer, "X axis positive.");
//     } else {
//       text_layer_set_text(s_output_layer, "X axis negative.");
//     }
//     break;
//   case ACCEL_AXIS_Y:
//     if (direction > 0) {
//       text_layer_set_text(s_output_layer, "Y axis positive.");
//     } else {
//       text_layer_set_text(s_output_layer, "Y axis negative.");
//     }
//     break;
//   case ACCEL_AXIS_Z:
//     if (direction > 0) {
//       text_layer_set_text(s_output_layer, "Z axis positive.");
//     } else {
//       text_layer_set_text(s_output_layer, "Z axis negative.");
//     }
//     break;
//   }
}

static void main_window_load(Window *window) {
  Layer *window_layer = window_get_root_layer(window);
  GRect window_bounds = layer_get_bounds(window_layer);

  // Create output TextLayer
  s_output_layer = text_layer_create(GRect(5, 0, window_bounds.size.w - 10, window_bounds.size.h));
  text_layer_set_font(s_output_layer, fonts_get_system_font(FONT_KEY_GOTHIC_24));
  text_layer_set_text(s_output_layer, "No data yet.");
  text_layer_set_overflow_mode(s_output_layer, GTextOverflowModeWordWrap);
  layer_add_child(window_layer, text_layer_get_layer(s_output_layer));
}

static void main_window_unload(Window *window) {
  // Destroy output TextLayer
  text_layer_destroy(s_output_layer);
}

static void init() {
  // Create main Window
  s_main_window = window_create();
  window_set_window_handlers(s_main_window, (WindowHandlers) {
    .load = main_window_load,
    .unload = main_window_unload
  });
  window_stack_push(s_main_window, true);

  // Subscribe to the accelerometer tap service
  accel_tap_service_subscribe(tap_handler);
}

static void deinit() {
  // Destroy main Window
  window_destroy(s_main_window);
  accel_tap_service_unsubscribe();
}

int main(void) {
  init();
  app_event_loop();
  deinit();
}
