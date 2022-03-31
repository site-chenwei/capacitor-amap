#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(CapacitorAMapPlugin, "CapacitorAMap",
           CAP_PLUGIN_METHOD(locate, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(weather, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(calculate, CAPPluginReturnPromise);
)
