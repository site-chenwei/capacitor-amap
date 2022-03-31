import { registerPlugin } from '@capacitor/core';

import type { CapacitorAMapPlugin } from './definitions';

const CapacitorAMap = registerPlugin<CapacitorAMapPlugin>('CapacitorAMap', {
  web: () => import('./web').then(m => new m.CapacitorAMapWeb()),
});

export * from './definitions';
export { CapacitorAMap };
