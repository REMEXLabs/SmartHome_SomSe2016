/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.android_wear.handler;

import static org.openhab.binding.android_wear.AndroidWearBindingConstants.*;

import java.io.IOException;
import java.util.Map;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.HSBType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ManagedThingProvider;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

/**
 * The {@link AndroidWearHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author hdm-sh-admm - Initial contribution
 */
public class AndroidWearHandler extends BaseThingHandler {
    private static final String GCM_SERVER_KEY = "AIzaSyBj-0nbbuJ85341vMSdqcaDEkSzGhLxJ2k";

    private Logger logger = LoggerFactory.getLogger(AndroidWearHandler.class);
    private Sender mGcmSender;
    private String mGcmId;

    private ManagedThingProvider managedThingProvider;

    public AndroidWearHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.info("AWB: handleCommand( " + channelUID.getId() + ", " + command.toString() + " )");

        if (channelUID.getId().equals(CHANNEL_VIBRATION)) {
            logger.info("AWB: vibration");
            Message message = new Message.Builder().addData("wear_data", "{\"vibrate\": true}").build();

            try {
                Result result = mGcmSender.sendNoRetry(message, mGcmId);
                logger.info("GCM result: " + result.getSuccess() + " succeded, " + result.getFailure() + "failed.");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (CHANNEL_WATCHFACE_INDICATORS.contains(channelUID.getId())) {
            if (command instanceof HSBType) {
                HSBType color = (HSBType) command;
                Message message = new Message.Builder().addData("wear_data", "{ \"watchface_indicator\": { "
                        + "\"indicator\": \"" + channelUID.getId() + "\", \"color\": " + color.getRGB() + "}}").build();
                try {
                    Result result = mGcmSender.sendNoRetry(message, mGcmId);
                    logger.info("GCM result: " + result.getSuccess() + " succeded, " + result.getFailure() + "failed.");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                logger.error("AWB: unknown indicator command type: " + command.getClass().getCanonicalName());
            }
        } else if (channelUID.getId().equals(CHANNEL_VOICECOMMAND)) {
            System.out.println("Voice Command Success");

            Message message = new Message.Builder()
                    .addData("wear_data", "{\"voiceSuccess\": " + command.toString() + "}").build();
            try {
                Result result = mGcmSender.sendNoRetry(message, mGcmId);
                System.out.println(
                        "GCM result: " + result.getSuccess() + " succeded, " + result.getFailure() + "failed.");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        super.handleConfigurationUpdate(configurationParameters);

        mGcmId = (String) configurationParameters.get(CONFIG_GCMID);

        logger.debug("configuration updated");
    }

    // called when the Handler is created
    @Override
    public void initialize() {
        super.initialize();

        Configuration config = getThing().getConfiguration();
        mGcmId = (String) config.get(CONFIG_GCMID);

        mGcmSender = new Sender(GCM_SERVER_KEY);
    }

    @Override
    public void dispose() {
        super.dispose();
        // called when the Handler is stopped
    }
}
