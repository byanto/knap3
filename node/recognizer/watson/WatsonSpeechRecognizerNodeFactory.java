package org.knime.base.node.audio3.node.recognizer.watson;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "WatsonSpeechRecognizer" Node.
 *
 *
 * @author Budi Yanto, KNIME.com
 */
public class WatsonSpeechRecognizerNodeFactory
        extends NodeFactory<WatsonSpeechRecognizerNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public WatsonSpeechRecognizerNodeModel createNodeModel() {
        return new WatsonSpeechRecognizerNodeModel();
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
    public NodeView<WatsonSpeechRecognizerNodeModel> createNodeView(final int viewIndex,
            final WatsonSpeechRecognizerNodeModel nodeModel) {
        return new WatsonSpeechRecognizerNodeView(nodeModel);
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
        return new WatsonSpeechRecognizerNodeDialog();
    }

}

