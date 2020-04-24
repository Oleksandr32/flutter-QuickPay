#import "QuickPayPlugin.h"
#if __has_include(<quick_pay/quick_pay-Swift.h>)
#import <quick_pay/quick_pay-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "quick_pay-Swift.h"
#endif

@implementation QuickPayPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftQuickPayPlugin registerWithRegistrar:registrar];
}
@end
