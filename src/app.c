#include <pebble.h>

static Window *s_main_window;
static TextLayer *s_output_layer;

static void main_window_load(Window *window) {
  Layer *window_layer = window_get_root_layer(window);
  GRect window_bounds = layer_get_bounds(window_layer);

  // Create output TextLayer
  s_output_layer = text_layer_create(GRect(5, 0, window_bounds.size.w - 10, window_bounds.size.h));
  text_layer_set_font(s_output_layer, fonts_get_system_font(FONT_KEY_GOTHIC_24));
  text_layer_set_overflow_mode(s_output_layer, GTextOverflowModeWordWrap);
  layer_add_child(window_layer, text_layer_get_layer(s_output_layer));
}

static void main_window_unload(Window *window) {
  // Destroy output TextLayer
  text_layer_destroy(s_output_layer);
}

void tick_handler(struct tm *tick_time, TimeUnits units_changed)
{
  //Allocate long-lived storage (required by TextLayer)
  static char buffer[] = "00:00";

  //Write the time to the buffer in a safe manner
  strftime(buffer, sizeof("00:00"), "%H:%M", tick_time);

  //Set the TextLayer to display the buffer
  text_layer_set_text(s_output_layer, buffer);
}

static void init() {
  // Create main Window
  s_main_window = window_create();
  window_set_window_handlers(s_main_window, (WindowHandlers) {
    .load = main_window_load,
    .unload = main_window_unload
  });
  window_stack_push(s_main_window, true);
  tick_timer_service_subscribe(MINUTE_UNIT, (TickHandler)tick_handler);
}

static void deinit() {
  // Destroy main Window
  window_destroy(s_main_window);
  tick_timer_service_unsubscribe();
}

int main(void) {
  init();
  app_event_loop();
  deinit();
}
