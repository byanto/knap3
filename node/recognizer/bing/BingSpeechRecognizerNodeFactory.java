package org.knime.base.node.audio3.node.recognizer.bing;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "BingSpeechRecognizer" Node.
 *
 *
 * @author Budi Yanto, KNIME.com
 */
public class BingSpeechRecognizerNodeFactory
        extends NodeFactory<BingSpeechRecognizerNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public BingSpeechRecognizerNodeModel createNodeModel() {
        return new BingSpeechRecognizerNodeModel();
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
    public NodeView<BingSpeechRecognizerNodeModel> createNodeView(final int viewIndex,
            final BingSpeechRecognizerNodeModel nodeModel) {
        return new BingSpeechRecognizerNodeView(nodeModel);
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
        return new BingSpeechRecognizerNodeDialog();
    }

}

