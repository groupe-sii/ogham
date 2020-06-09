package fr.sii.ogham.html.inliner.impl.regexp;

/**
 * Common constants for inlining of images included through CSS properties (such
 * as background, list-style, ...).
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public final class CssImageInlinerConstants {
	/**
	 * <p>
	 * The CSS property name to indicate which strategy for inlining to apply if
	 * the default one is not appropriated.
	 * </p>
	 * 
	 * For example:
	 * 
	 * <pre>
	 * <code>
	 * 	background: url(...);
	 * 	--inline-image: base64;
	 * </code>
	 * </pre>
	 * 
	 * It forces to use <code>base64</code> inlining for this image.
	 * 
	 * 
	 * <p>
	 * You can also skip inlining by using "skip" value:
	 * 
	 * <pre>
	 * <code>
	 * 	background: url(...);
	 * 	--inline-image: skip;
	 * </code>
	 * </pre>
	 * 
	 * <p>
	 * It is possible to have several images to inline for the same CSS rule.
	 * Therefore, you can indicate an inline mode per image:
	 * 
	 * <pre>
	 * <code>
	 * .rule {
	 * 		background: url('path/to/top.png'), url('path/to/bottom.gif');
	 * 		list-style-image: url('path/to/list.gif');
	 * 		cursor: url('path/to/cursor.png');
	 * 		--inline-image: top=attach bottom=base64 list=skip;
	 * }
	 * </code>
	 * </pre>
	 * 
	 * {@code --inline-image} indicates that:
	 * <ul>
	 * <li>Image 'path/to/top.png' should be inlined using {@code attach}
	 * mode</li>
	 * <li>Image 'path/to/bottom.gif' should be inlined using {@code base64}
	 * mode</li>
	 * <li>Image 'path/to/list.gif' should not be inlined</li>
	 * <li></li>
	 * </ul>
	 * 
	 * As nothing is specified for 'path/to/cursor.png', the default inline mode
	 * is used.
	 * 
	 * <strong>NOTE:</strong> Only the name is provided but you can also target
	 * an image by providing any matching path
	 * ({@code --inline-image: to/top.png=attach} also targets 'top' image).
	 * This can be useful if you have several images with the same name but in
	 * different folders.
	 * 
	 * @see InlineModes
	 */
	public static final String INLINE_MODE_PROPERTY = "--inline-image";

	/**
	 * Property to mark an image as already inlined in order to not process it
	 * again
	 */
	public static final String INLINED_URL_FUNC = "--image-inlined-url";

	/**
	 * Interface for defining an inline mode
	 * 
	 * @author Aurélien Baudet
	 *
	 */
	public interface InlineMode {
		/**
		 * The inline mode value
		 * 
		 * @return inline mode value
		 */
		String mode();

		/**
		 * Indicates if the mode is the same as the current mode.
		 * 
		 * @param mode
		 *            the mode to check if same
		 * @return true if same value, false otherwise
		 */
		boolean is(String mode);
	}

	/**
	 * Provide predefined inline modes
	 * 
	 * @author Aurélien Baudet
	 *
	 */
	public enum InlineModes implements InlineMode {
		/**
		 * Attach the image to the email and references it in the HTML using a
		 * Content-ID (CID).
		 */
		ATTACH("attach"),
		/**
		 * Encode the image content to a base64 string that is directly used by
		 * image {@code src} attribute
		 */
		BASE64("base64"),
		/**
		 * Do not inline the image
		 */
		SKIP("skip");

		private final String mode;

		InlineModes(String mode) {
			this.mode = mode;
		}

		@Override
		public String mode() {
			return mode;
		}

		@Override
		public boolean is(String mode) {
			return this.mode.equals(mode);
		}
	}

	private CssImageInlinerConstants() {
		super();
	}
}
