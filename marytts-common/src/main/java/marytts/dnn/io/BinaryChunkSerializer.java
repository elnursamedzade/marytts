package marytts.dnn.io;

import marytts.io.serializer.Serializer;

import marytts.data.item.Item;
import marytts.data.Sequence;
import marytts.data.SupportedSequenceType;
import marytts.data.Utterance;
import marytts.dnn.features.FeatureChunk;

import marytts.io.MaryIOException;
import java.nio.ByteBuffer;

// FIXME: for now we use the static feature computer....
import marytts.features.FeatureComputer;

/**
 * Feature serializer to generate TSV format output. There is not import from it
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le
 *         Maguer</a>
 */
public class BinaryChunkSerializer implements Serializer {

    private String selected_sequence;

    /**
     * Constructor
     *
     */
    public BinaryChunkSerializer() {
	setSelectedSequence(SupportedSequenceType.NORMALISED_FEATURES);
    }

    public String getSelectedSequence() {
	return selected_sequence;
    }

    public void setSelectedSequence(String selected_sequence) {
	this.selected_sequence = selected_sequence;
    }


    /**
     * Generate the TSV output from the utterance. Only the feature sequence is
     * used !
     *
     * @param utt
     *            the utterance containing the feature sequence
     * @return the TSV formatted feature sequence
     * @throws MaryIOException
     *             if anything is going wrong
     */
    public Object export(Utterance utt) throws MaryIOException {
        if (!utt.hasSequence(getSelectedSequence())) {
            throw new MaryIOException("Current utterance doesn't have the desired sequence",
                                      null);
        }

	// Compute size
	int size = 0;
	for (FeatureChunk chunk: (Sequence<FeatureChunk>) utt.getSequence(selected_sequence)) {
	    size += chunk.getData().numBytes();
	}

	// Copy data information
	ByteBuffer dst = ByteBuffer.allocate(size);
	for (FeatureChunk chunk: (Sequence<FeatureChunk>) utt.getSequence(selected_sequence)) {
	    chunk.getData().writeTo(dst);
	}

	// Flip buffer to be able to validate header
	dst.flip();

	return dst; // Double check !

    }

    /**
     * Unsupported operation ! We can't import from a TSV formatted input.
     *
     * @param content
     *            unused
     * @return nothing
     * @throws MaryIOException
     *             never done
     */
    public Utterance load(String content) throws MaryIOException {
        throw new UnsupportedOperationException();
    }
}