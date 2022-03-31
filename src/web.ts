import {WebPlugin} from '@capacitor/core';

import type {CapacitorAMapPlugin, Location, WeatherInfo, PermissionStatus} from './definitions';

export class CapacitorAMapWeb extends WebPlugin implements CapacitorAMapPlugin {
    async locate(): Promise<Location | undefined> {
        return undefined;
    }

    async weather(): Promise<WeatherInfo | undefined> {
        return undefined;
    }

    async calculate(): Promise<{ distance: number } | undefined> {
        return undefined;
    }

    async checkPermissions(): Promise<PermissionStatus> {
        throw this.unimplemented('Not implemented on web.');
    }

    async requestPermissions(): Promise<PermissionStatus> {
        throw this.unimplemented('Not implemented on web.');
    }

}
