package org.knime.base.node.audio3.node.viewer;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "AudioViewer" Node.
 * 
 *
 * @author Budi Yanto, KNIME.com
 */
public class AudioViewerNodeFactory 
        extends NodeFactory<AudioViewerNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public AudioViewerNodeModel createNodeModel() {
        return new AudioViewerNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<AudioViewerNodeModel> createNodeView(final int viewIndex,
            final AudioViewerNodeModel nodeModel) {
        return new AudioViewerNodeView(nodeModel);
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
        return new AudioViewerNodeDialog();
    }

}

