package org.knime.base.node.audio3.node.recognizer.cmusphinx;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "CMUSphinxRecognizer" Node.
 * 
 *
 * @author Budi Yanto, KNIME.com
 */
public class CMUSphinxRecognizerNodeFactory 
        extends NodeFactory<CMUSphinxRecognizerNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public CMUSphinxRecognizerNodeModel createNodeModel() {
        return new CMUSphinxRecognizerNodeModel();
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
    public NodeView<CMUSphinxRecognizerNodeModel> createNodeView(final int viewIndex,
            final CMUSphinxRecognizerNodeModel nodeModel) {
        return new CMUSphinxRecognizerNodeView(nodeModel);
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
        return new CMUSphinxRecognizerNodeDialog();
    }

}

