import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.logisty.core',
  appName: 'logisty',
  webDir: 'dist',
  plugins: {
    LocalNotifications: {
      smallIcon: "ic_stat_icon_config_sample",
      iconColor: "#488AFF",
      sound: "beep.wav",
    },
  },
  server: {
    androidScheme: 'http',
    cleartext: true
  },
  android: {
    useLegacyBridge: true,
    allowMixedContent: true
  },
};

export default config;
