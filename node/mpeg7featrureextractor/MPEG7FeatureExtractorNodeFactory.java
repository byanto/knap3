package org.knime.base.node.audio3.node.mpeg7featrureextractor;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MPEG7FeatureExtractor" Node.
 * 
 *
 * @author Budi Yanto, KNIME.com
 */
public class MPEG7FeatureExtractorNodeFactory 
        extends NodeFactory<MPEG7FeatureExtractorNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MPEG7FeatureExtractorNodeModel createNodeModel() {
        return new MPEG7FeatureExtractorNodeModel();
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
    public NodeView<MPEG7FeatureExtractorNodeModel> createNodeView(final int viewIndex,
            final MPEG7FeatureExtractorNodeModel nodeModel) {
        return new MPEG7FeatureExtractorNodeView(nodeModel);
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
        return new MPEG7FeatureExtractorNodeDialog();
    }

}

