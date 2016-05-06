package org.knime.base.node.audio3.node.reader;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "AudioReader" Node.
 *
 *
 * @author Budi Yanto, KNIME.com
 */
public class AudioReaderNodeFactory
        extends NodeFactory<AudioReaderNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public AudioReaderNodeModel createNodeModel() {
        return new AudioReaderNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<AudioReaderNodeModel> createNodeView(final int viewIndex,
            final AudioReaderNodeModel nodeModel) {
        return new AudioReaderNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new AudioReaderNodeDialog();
    }

}

