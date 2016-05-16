package org.knime.base.node.audio3.node.dataextractor;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "DataExtractor" Node.
 *
 *
 * @author Budi Yanto, KNIME.com
 */
public class DataExtractorNodeFactory
        extends NodeFactory<DataExtractorNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public DataExtractorNodeModel createNodeModel() {
        return new DataExtractorNodeModel();
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
    public NodeView<DataExtractorNodeModel> createNodeView(final int viewIndex,
            final DataExtractorNodeModel nodeModel) {
        return new DataExtractorNodeView(nodeModel);
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
        return new DataExtractorNodeDialog();
    }

}

