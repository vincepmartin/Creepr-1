#include <pebble.h>

#define KEY_BUTTON    0
#define KEY_VIBRATE   1

#define BUTTON_UP     0
#define BUTTON_SELECT 1
#define BUTTON_DOWN   2

static Window *s_main_window;
static TextLayer *s_output_layer;
static bool InDanger = false;
static GFont s_time_font;
static InverterLayer *inv_layer;
static bool Inversion = false;


/*********************** Clockface Functionality ******************************/
void tick_handler(struct tm *tick_time, TimeUnits units_changed)
{
  //Allocate long-lived storage (required by TextLayer)
  static char buffer[] = "00:00";
  char timeString[] = "%H:%M:%S";
  if (InDanger) {
    strcpy(timeString, "%H.%M.%S");
  }

  //Write the time to the buffer in a safe manner
  strftime(buffer, sizeof("00:00"), timeString , tick_time);

  //Set the TextLayer to display the buffer
  text_layer_set_text(s_output_layer, buffer);
}

/******************************* AppMessage ***********************************/

static void send(int key, int message) {
  DictionaryIterator *iter;
  app_message_outbox_begin(&iter);

  dict_write_int(iter, key, &message, sizeof(int), true);

  app_message_outbox_send();
}

//Vibrate function
static void inbox_received_handler(DictionaryIterator *iterator, void *context) {
  // Get the first pair
  Tuple *t = dict_read_first(iterator);

  // Process all pairs present
  while(t != NULL) {
    // Process this pair's key
    switch(t->key) {
      case KEY_VIBRATE:
        // Trigger vibration
        text_layer_set_text(s_output_layer, "Vibrate!");
        vibes_short_pulse();
        break;
      default:
        APP_LOG(APP_LOG_LEVEL_INFO, "Unknown key: %d", (int)t->key);
        break;
    }

    // Get next pair, if any
    t = dict_read_next(iterator);
  }
}

//

//Debug
static void inbox_dropped_handler(AppMessageResult reason, void *context) {
  APP_LOG(APP_LOG_LEVEL_ERROR, "Message dropped!");
}

static void outbox_failed_handler(DictionaryIterator *iterator, AppMessageResult reason, void *context) {
  APP_LOG(APP_LOG_LEVEL_ERROR, "Outbox send failed!");
}

static void outbox_sent_handler(DictionaryIterator *iterator, void *context) {
  APP_LOG(APP_LOG_LEVEL_INFO, "Outbox send success!");
}
//
/********************************* Buttons ************************************/

static void toggleInvert() {
  if (Inversion) {
    inverter_layer_destroy(inv_layer);
    inv_layer = 0;
  }
  else {
    inv_layer = inverter_layer_create(GRect(0, 0, 180, 180));
    layer_add_child(window_get_root_layer(s_main_window), (Layer*) inv_layer);
  }
  Inversion = !Inversion;
}

static void select_click_handler(ClickRecognizerRef recognizer, void *context) {
  send(KEY_BUTTON, BUTTON_SELECT);
  toggleInvert();
}

static void up_click_handler(ClickRecognizerRef recognizer, void *context) {
  send(KEY_BUTTON, BUTTON_UP);
  toggleInvert();
}

static void down_click_handler(ClickRecognizerRef recognizer, void *context) {
  send(KEY_BUTTON, BUTTON_DOWN);
  toggleInvert();
}

static void click_config_provider(void *context) {
  // Assign button handlers
  window_single_click_subscribe(BUTTON_ID_SELECT, select_click_handler);
  window_single_click_subscribe(BUTTON_ID_UP, up_click_handler);
  window_single_click_subscribe(BUTTON_ID_DOWN, down_click_handler);
}

/******************************* main_window **********************************/

static void main_window_load(Window *window) {
  Layer *window_layer = window_get_root_layer(window);
  GRect window_bounds = layer_get_bounds(window_layer);
  // Create main TextLayer
  s_time_font = fonts_load_custom_font(resource_get_handle(RESOURCE_ID_PIXEL_FONT_24));
  s_output_layer = text_layer_create(GRect(5, 50, window_bounds.size.w - 10, window_bounds.size.h));
  text_layer_set_font(s_output_layer, s_time_font);
  text_layer_set_overflow_mode(s_output_layer, GTextOverflowModeWordWrap);

  text_layer_set_text_alignment(s_output_layer, GTextAlignmentCenter);
  layer_add_child(window_layer, text_layer_get_layer(s_output_layer));
}

static void main_window_unload(Window *window) {
  // Destroy main TextLayer
  text_layer_destroy(s_output_layer);
}

static void init(void) {
  // Register callbacks
  app_message_register_inbox_received(inbox_received_handler);
  app_message_register_inbox_dropped(inbox_dropped_handler);
  app_message_register_outbox_failed(outbox_failed_handler);
  app_message_register_outbox_sent(outbox_sent_handler);

  // Open AppMessage
  app_message_open(app_message_inbox_size_maximum(), app_message_outbox_size_maximum());

  // Create main Window
  s_main_window = window_create();
  window_set_click_config_provider(s_main_window, click_config_provider);
  window_set_window_handlers(s_main_window, (WindowHandlers) {
    .load = main_window_load,
    .unload = main_window_unload,
  });
  window_stack_push(s_main_window, true);

  tick_timer_service_subscribe(MINUTE_UNIT, (TickHandler)tick_handler);
  
  //Inverter layer
  inv_layer = inverter_layer_create(GRect(0, 0, 180, 180));
  layer_add_child(window_get_root_layer(s_main_window), (Layer*) inv_layer);
}


static void deinit(void) {
  // Destroy main Window
  window_destroy(s_main_window);
  tick_timer_service_unsubscribe();
  inverter_layer_destroy(inv_layer);
}

int main(void) {
  init();
  app_event_loop();
  deinit();
}
