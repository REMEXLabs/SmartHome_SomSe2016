/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.android_wear;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link AndroidWearBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author hdm-sh-admm - Initial contribution
 */
public class AndroidWearBindingConstants {

    public static final String BINDING_ID = "androidwear";

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_WATCH = new ThingTypeUID(BINDING_ID, "watch");

    // List of all Channel ids
    public final static String CHANNEL_VOICECOMMAND = "voicecommand";
    public final static String CHANNEL_VIBRATION = "vibration";
    public final static String CHANNEL_WATCHFACE_INDICATOR_1 = "watchface_indicator_1";
    public final static String CHANNEL_WATCHFACE_INDICATOR_2 = "watchface_indicator_2";
    public final static String CHANNEL_WATCHFACE_INDICATOR_3 = "watchface_indicator_3";
    public final static String CHANNEL_WATCHFACE_INDICATOR_4 = "watchface_indicator_4";
    public final static String CHANNEL_WATCHFACE_INDICATOR_5 = "watchface_indicator_5";
    public final static String CHANNEL_WATCHFACE_INDICATOR_6 = "watchface_indicator_6";

    public final static String CONFIG_GCMID = "gcmid";

    public final static Set<String> CHANNEL_WATCHFACE_INDICATORS = new HashSet<String>(Arrays.asList(
            new String[] { CHANNEL_WATCHFACE_INDICATOR_1, CHANNEL_WATCHFACE_INDICATOR_2, CHANNEL_WATCHFACE_INDICATOR_3,
                    CHANNEL_WATCHFACE_INDICATOR_4, CHANNEL_WATCHFACE_INDICATOR_5, CHANNEL_WATCHFACE_INDICATOR_6 }));

}
