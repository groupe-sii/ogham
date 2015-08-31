package fr.sii.ogham.html.inliner;

/**
 * Common constants for image inlining.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public interface ImageInlinerConstants {
	/**
	 * <p>
	 * The attribute name to indicate which strategy for inlining to apply if
	 * the default one is not appropriated.
	 * </p>
	 * 
	 * For example:
	 * 
	 * <pre>
	 * {@code
	 * 		<img src="..." data-inline-mode="base64" />
	 * }
	 * </pre>
	 * 
	 * It forces to use <code>base64</code> inlining for this image
	 */
	public static final String INLINE_MODE_ATTR = "data-inline-mode";

	/**
	 * <p>
	 * The attribute name to indicate the modes that are not allowed for
	 * inlining. The value can contain several modes separated by colon (,).
	 * This attribute is complementary with the previous one: it is useful in
	 * order to not force statically one mode directly in the template. It just
	 * tells that one or several modes can't be applied.
	 * </p>
	 * 
	 * For example :
	 * 
	 * <pre>
	 * {@code
	 * 		<img src="..." data-skip-inline="attach" />
	 * }
	 * </pre>
	 * 
	 * It indicates that all modes are applicable except the mode
	 * <code>attach</code>
	 */
	public static final String SKIP_INLINE_ATTR = "data-skip-inline";

	/**
	 * Value for skipping all inlining modes. The image will not be inlined at
	 * all.
	 */
	public static final String SKIP_INLINE_ALL_VALUE = "true";

}
