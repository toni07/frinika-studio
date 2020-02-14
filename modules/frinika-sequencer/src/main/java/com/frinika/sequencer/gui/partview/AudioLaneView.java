/*
 * Created on Jun 22, 2006
 *
 * Copyright (c) 2006 P.J.Leonard
 * 
 * http://www.frinika.com
 * 
 * This file is part of Frinika.
 * 
 * Frinika is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * Frinika is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with Frinika; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.frinika.sequencer.gui.partview;

import com.frinika.audio.gui.ListProvider;
import com.frinika.base.FrinikaAudioServer;
import com.frinika.base.FrinikaAudioSystem;
import com.frinika.sequencer.gui.PopupClient;
import com.frinika.sequencer.gui.PopupSelectorButton;
import com.frinika.sequencer.model.AudioLane;
import java.awt.Dimension;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.JComponent;
import uk.org.toot.audio.core.AudioProcess;

public class AudioLaneView extends LaneView
{
    private static final Logger logger = Logger.getLogger(AudioLaneView.class.getName());
    AudioProcess audioIn;
    String name = "... Choose input ...";

    public AudioLaneView(AudioLane lane) {
        super(lane);
        init();
    }

    private static final long serialVersionUID = 1L;

    @Override
    protected void makeButtons() {

        JComponent but = createDeviceSelector();
        add(but, gc);
        gc.weighty = 1.0;
        add(new Box.Filler(new Dimension(0, 0),
                new Dimension(10000, 10000), new Dimension(10000, 10000)),
                gc);

    }

    PopupSelectorButton createDeviceSelector() {
        audioIn = ((AudioLane) lane).getAudioInDevice();

        // Device selector
        // ------------------------------------------------------------------------------------
        final FrinikaAudioServer currentlyRunningAudioServer = FrinikaAudioSystem.getAudioServer();
        ListProvider resource = new ListProvider() {
            @Override
            public Object[] getList() {
                // TODO connections setup
//				Vector<AudioDeviceHandle> vec = AudioHub.getAudioInHandles();
//				AudioDeviceHandle list[] = new AudioDeviceHandle[vec.size()];
//				list=vec.toArray(list);
                logger.log(Level.INFO, "Currently running audio server: ", currentlyRunningAudioServer);
                List<String> vec = currentlyRunningAudioServer.getAvailableInputNames();
                logger.log(Level.INFO, "Available audio inputs: ", vec);
                String list[] = new String[vec.size()];
                list = vec.toArray(list);

                //	int ii=0;
                //	for (AudioDeviceHandle h:vec) {
                //		list[ii++]=h;
                //	}
                return list;
            }
        };

        PopupClient client = new PopupClient() {
            @Override
            public void fireSelected(PopupSelectorButton but, Object o, int cnt) {
                AudioProcess in;
                try {
                    in = currentlyRunningAudioServer.openAudioInput((String) o, null);
                    ((AudioLane) lane).setAudioInDevice(in);
                    name = (String) o;
                    if (in != audioIn) {
                        init();
                    }

                }
                catch (Exception e)
                {
                    logger.log(Level.SEVERE, "Error: ", e);
                }
            }
        };

//		if (audioIn != null)
//			name = audioIn.toString();
//		else
//			name = "null";
        return new PopupSelectorButton(resource, client, name);
    }
}
