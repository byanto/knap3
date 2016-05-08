/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   May 8, 2016 (budiyanto): created
 */
package org.knime.base.node.audio3.util;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.knime.base.node.audio3.data.Audio;
import org.knime.base.node.audio3.data.SampleChunk;
import org.knime.base.node.audio3.data.SampleChunkFactory;
import org.knime.core.node.NodeLogger;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public class AudioPlayer implements Runnable{

    private static final NodeLogger LOGGER = NodeLogger.getLogger(AudioPlayer.class);

    private final Audio m_audio;
    private SourceDataLine m_line = null;
    private boolean m_started = false;
    private Mode m_mode = Mode.PLAY;
    private List<AudioEventListener> m_listeners = new ArrayList<AudioEventListener>();

    /**
     * Current state of the audio player
     */
    public enum Mode {
        /** The audio player is playing */
        PLAY,

        /** The audio player is paused */
        PAUSE,

        /** The audio player is stopped */
        STOP
    }

    public AudioPlayer(final Audio audio){
        m_audio = audio;
    }

    private void setMode(final Mode mode){
        m_mode = mode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        setMode(Mode.PLAY);
        if(!m_started){
            m_started = true;
            try {
                openSourceDataLine();
                final SampleChunkFactory factory = new SampleChunkFactory(
                    m_audio, true, 1024);
                SampleChunk chunk = null;
                boolean ended = false;
                while(!ended && m_mode != Mode.STOP){
                    if(m_mode == Mode.PLAY){
                        chunk = factory.nextSampleChunk();
                        if(chunk == null){
                            ended = true;
                            continue;
                        }
                        fireBeforePlay(chunk);
                        playAudioChunk(chunk);
                        fireAfterPlay(chunk);
                    } else{
                        Thread.sleep(500);
                    }
                }
                firePlayEnded();
                setMode(Mode.STOP);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
            } finally {
                closeSourceDataLine();
            }
        } else {
            setMode(Mode.PLAY);
        }
    }

    private void openSourceDataLine() throws LineUnavailableException{
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class,
            m_audio.getAudioFileFormat().getFormat());
        m_line = (SourceDataLine) AudioSystem.getLine(info);
        m_line.open(m_line.getFormat());
        m_line.start();
        LOGGER.debug("Successfully opened SourceDataLine: " + m_line.getFormat());
    }

    private void closeSourceDataLine(){
        if(m_line != null){
            m_line.drain();
            m_line.close();
            m_line = null;
        }
    }

    private void playAudioChunk(final SampleChunk chunk){
        final byte[] samples = chunk.getSamples();
        m_line.write(samples, 0, samples.length);
    }

    /**
     * Play the audio player
     */
    public void play(){
        setMode(Mode.PLAY);
    }

    /**
     * Pause the audio player
     */
    public void pause(){
        setMode(Mode.PAUSE);
    }

    /**
     * Stop the audio player
     */
    public void stop(){
        setMode(Mode.STOP);
    }

    /**
     * @return <code>true</code> if the player is currently paused,
     * otherwise <code>false</code>
     */
    public boolean isPaused(){
        return m_mode == Mode.PAUSE;
    }

    /**
     * @return <code>true</code> is the player is currently playing,
     * otherwise <code>false</code>
     */
    public boolean isPlaying(){
        return m_mode == Mode.PLAY;
    }

    /**
     * Starts the audio player in a new thread
     */
    public void start(){
        new Thread(this).start();
    }

    /**
     * Adds the given listener to the listener list
     * @param listener the listener to add
     */
    public void addAudioEventListener(final AudioEventListener listener){
        m_listeners.add(listener);
    }

    /**
     * Remove the given listener from the listener list
     * @param listener the listener to remove
     */
    public void removeAudioEventListener(final AudioEventListener listener){
        m_listeners.remove(listener);
    }

    /**
     * Fires an event indicating that the given audio chunk will be played
     * @param chunk the chunk that will be played
     */
    private void fireBeforePlay(final SampleChunk chunk){
        for(final AudioEventListener listener : m_listeners){
            listener.beforePlay(chunk);
        }
    }

    /**
     * Fires an event indicating that the given audio chunk has been played
     * @param chunk the chunk that has been played
     */
    private void fireAfterPlay(final SampleChunk chunk){
        for(final AudioEventListener listener : m_listeners){
            listener.afterPlay(chunk);
        }
    }

    /**
     * Fires an event indicating that the whole audio has been played
     */
    private void firePlayEnded(){
        for(final AudioEventListener listener : m_listeners){
            listener.playEnded();
        }
    }

}
