/**
 * Almost like an audio track, but plays a different sound than the one it records
 */
package com.frinika.sequencer.model;

import com.frinika.audio.io.AudioWriter;
import com.frinika.audio.toot.AudioPeakMonitor;
import com.frinika.global.property.FrinikaGlobalProperties;
import com.frinika.localization.CurrentLocale;
import com.frinika.model.EditHistoryRecordable;
import com.frinika.sequencer.FrinikaSequencer;
import com.frinika.sequencer.SequencerListener;
import com.frinika.sequencer.gui.partview.TriggerSoundTrackView;
import com.frinika.sequencer.project.AbstractProjectContainer;
import rasmus.midi.provider.RasmusSynthesizer;
import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.core.ChannelFormat;
import uk.org.toot.audio.mixer.MixControls;
import uk.org.toot.audio.server.AudioServer;
import uk.org.toot.audio.server.IOAudioProcess;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Logger;

public class TriggerSoundTrack extends Lane implements RecordableLane,
        SequencerListener {

    private static final Logger logger = Logger.getLogger(TriggerSoundTrack.class.getName());
    transient AudioProcess audioInProcess = null; // audio input
    protected transient AudioProcess audioInsert = null;
    static Icon icon = new javax.swing.ImageIcon(RasmusSynthesizer.class
            .getResource("/icons/audiolane.png"));

    // THIS IS TEMPORARY - have to be able to add mixerslots dynamically
    // we still need a unique id. But maybe move this up to Lane ?
    public static int stripNo = 1;

    /**
     * Audio Process to be connected to the project mixer
     */
    transient AudioProcess audioProcess;

    transient AudioPeakMonitor peakMonitor;

    transient boolean armed = false; // armed for recording

    transient boolean isRecording = false; // is sequencer running && armed //
    // recording

    transient boolean hasRecorded = false; // true if any data has been saved

    transient AudioWriter writer = null; // direct to disk writer

    transient private long recordStartTimeInMicros;

    transient private FrinikaSequencer sequencer;

    transient private MixControls mixerControls = null;
    transient int stripInt = -1;

    private static final long serialVersionUID = 1L;
    protected transient File clipFile;

    static int nameCount = 0;

    public TriggerSoundTrack(AbstractProjectContainer project) {
        super("Trigger " + nameCount++, project);
        attachAudioProcessToMixer();
    }

    public void dispose() {
        frinikaProject.getSequencer().removeSequencerListener(this);
        writer.discard();
    }

    @Override
    public void removeFromModel() {
        frinikaProject.removeStrip(stripInt + "");
        super.removeFromModel();
    }

    private void attachAudioProcessToMixer() {
        peakMonitor = new AudioPeakMonitor();

        audioProcess = new AudioProcess() {
            @Override
            public void close() {
            }

            @Override
            public void open() {
            }

            @Override
            public int processAudio(AudioBuffer buffer) {
                // Process audio of all parts in this lane
                // do we need to zero the buffer here ?
                //toni07 this byteBuffer contains the data this has just been heard
                System.out.println("processAudio 0");
                if (armed)
                {
                    System.out.println("processAudio 1");
                    audioInProcess.processAudio(buffer);
                    peakMonitor.processAudio(buffer);
                    if (audioInsert != null) {
                        audioInsert.processAudio(buffer);
                    }
                    if (isRecording) {
                        // TODO handle DISCONNECT
                        writer.processAudio(buffer);
                        hasRecorded = true;
                    }
                    if (FrinikaGlobalProperties.DIRECT_MONITORING.getValue()) {
                        buffer.makeSilence();
                    }
                }
                else
                {
                    System.out.println("processAudio 1 not armed");
                    if (frinikaProject.getSequencer().isRunning()) {
                        buffer.setChannelFormat(ChannelFormat.STEREO);
                        buffer.makeSilence();
                        for (Part part : getParts()) {
                            if (((AudioPart) part).getAudioProcess() != null) {
                                ((AudioPart) part).getAudioProcess()
                                        .processAudio(buffer);
                            }
                        }
                        peakMonitor.processAudio(buffer);
                    } else {
                        buffer.makeSilence();
                    }
                }

                buffer.setMetaInfo(channelLabel);
                return AUDIO_OK;
            }
        };

        try {
            mixerControls = frinikaProject.addMixerInput(audioProcess, (stripInt = stripNo++) + "");

            // project.getMixer().getStrip((stripNo++) + "").setInputProcess(
            // audioProcess);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        sequencer = frinikaProject.getSequencer();
        sequencer.addSequencerListener(this);
    }

    @Override
    public void restoreFromClone(EditHistoryRecordable object) {
        System.out.println("AudioLane restroeFromClone");
    }

    @Override
    public Selectable deepCopy(Selectable parent) {
        return null;
    }

    @Override
    public void deepMove(long tick) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isRecording() {
        return armed;
    }

    @Override
    public boolean isMute() {
        return mixerControls.isMute();
    }

    public boolean isSolo() {
        return mixerControls.isSolo();
    }

    @Override
    public void setRecording(boolean b)
    {
        if (b && audioInProcess == null) {
            armed = false;
            frinikaProject.getMessageHandler().message(CurrentLocale.getMessage("recording.please_select_audio_input"));
            return;
        }

        armed = b;
    }

    @Override
    public void setMute(boolean b) {
        mixerControls.getMuteControl().setValue(b);
    }

    public void setSolo(boolean b) {
        mixerControls.getSoloControl().setValue(b);
    }

    private void readObject(ObjectInputStream in)
            throws ClassNotFoundException, IOException
    {
        in.defaultReadObject();

        // attachAudioProcessToMixer
        peakMonitor = new AudioPeakMonitor();

        audioProcess = new AudioProcess() {
            @Override
            public void close() {
            }

            @Override
            public void open() {
            }

            @Override
            public int processAudio(AudioBuffer buffer)
            {
                // Process audio of all parts in this lane
                // do we need to zero the buffer here ?

                if (armed)
                {
                    logger.info("processAudio 2");
                    audioInProcess.processAudio(buffer);
                    peakMonitor.processAudio(buffer);
                    if (audioInsert != null) {
                        audioInsert.processAudio(buffer);
                    }
                    if (isRecording) {
                        // TODO handle DISCONNECT
                        writer.processAudio(buffer);
                        hasRecorded = true;
                    }
                    if (FrinikaGlobalProperties.DIRECT_MONITORING.getValue()) {
                        buffer.makeSilence();
                    }
                }
                else
                {
                    if (frinikaProject.getSequencer().isRunning()) {
                        buffer.setChannelFormat(ChannelFormat.STEREO);
                        buffer.makeSilence();
                        for (Part part : getParts()) {
                            if (((AudioPart) part).getAudioProcess() != null) {
                                ((AudioPart) part).getAudioProcess()
                                        .processAudio(buffer);
                            }
                        }
                        peakMonitor.processAudio(buffer);
                    } else {
                        buffer.makeSilence();
                    }
                }

                buffer.setMetaInfo(channelLabel);
                return AUDIO_OK;
            }
        };

        try {
            mixerControls = project.addMixerInput(audioProcess, (stripInt = stripNo++) + "");

            // project.getMixer().getStrip((stripNo++) + "").setInputProcess(
            // audioProcess);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        sequencer = project.getSequencer();
        sequencer.addSequencerListener(this);
    }

    public AudioProcess getAudioInDevice() {
        return audioInProcess;
    }

    public void setAudioInDevice(AudioProcess handle) {
        audioInProcess = handle;
        if (writer != null) {
            writer.close();
        }
        writer = newAudioWriter();
    }

    @Override
    public double getMonitorValue() {
        return peakMonitor.getPeak();
    }

    /**
     * Creates a new audio file handle to save a clip.
     */
    public AudioWriter newAudioWriter()
    {
        clipFile = newFilename();

        AudioFormat format = new AudioFormat(
                FrinikaGlobalProperties.getSampleRate(),
                16,
                ((IOAudioProcess) audioInProcess).getChannelFormat().getCount(),
                true, false);

        try {
            return new AudioWriter(clipFile, format);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public File newFilename() {
        AbstractProjectContainer proj = getProject();

        File audioDir = proj.getAudioDirectory();
        String audioFileName = getName() + ".wav";
        File clipFile = new File(audioDir, audioFileName);
        int cnt = 1;
        while (clipFile.exists()) {
            audioFileName = getName() + "_" + (cnt++) + ".wav";
            clipFile = new File(audioDir, audioFileName);
        }
        return clipFile;
    }

    @Override
    public void beforeStart() {
    }

    @Override
    public void start()
    {
        isRecording = frinikaProject.getSequencer().isRecording();
        if (isRecording) {
            recordStartTimeInMicros = sequencer.getMicrosecondPosition();
        }
    }

    @Override
    public void stop()
    {
        isRecording = false;
        if (hasRecorded)
        {
            frinikaProject.getEditHistoryContainer().mark(
                    CurrentLocale.getMessage("sequencer.audiolane.record"));

            writer.close();
            hasRecorded = false;
            AudioServer server = frinikaProject.getAudioServer();
            int latencyInframes = frinikaProject.getAudioServer().getTotalLatencyFrames();

            System.out.println(" latency in frames is " + latencyInframes);
            double latencyInMicros = latencyInframes * 1000000.0
                    / server.getSampleRate();

            // shift record time back in time because we play along with a delay
            // output.
            recordStartTimeInMicros -= latencyInMicros;
            // TODO Latency compensation (toni07)
            AudioPart part;
            try {
                part = new AudioPart(this, writer.getFile(),
                        recordStartTimeInMicros);
                part.onLoad();
                writer = newAudioWriter();

            } catch (FileNotFoundException e) {
                e.printStackTrace();

            }
            frinikaProject.getEditHistoryContainer().notifyEditHistoryListeners();
        }
    }

    public MixControls getMixerControls() {
        return mixerControls;
    }

    @Override
    public Part createPart()
    {
        try
        {
            throw new Exception(" Attempt to create an AudiPart");
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }
}
