package fr.sii.ogham.html.inliner;

/**
 * Common constants for CSS inlining.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public final class CssInlinerConstants {
	/**
	 * <p>
	 * The attribute name to indicate which strategy for inlining to apply if
	 * the default one is not appropriated.
	 * </p>
	 * 
	 * <p>
	 * <strong>NOTE:</strong> Currently there only one available strategy but
	 * later, there could be several ways to inline styles.
	 * 
	 * <p>
	 * For example:
	 * 
	 * CSS rules:
	 * 
	 * <pre>
	 * {@code
	 * .white {
	 *   color: #fff;
	 * }
	 * }
	 * </pre>
	 * 
	 * <p>
	 * You can skip inlining by using "skip" value:
	 * 
	 * <pre>
	 * {@code
	 * 		<span class="white" data-inline-styles="skip"></span>
	 * }
	 * </pre>
	 * 
	 * 
	 * @see InlineModes
	 */
	public static final String INLINE_MODE_ATTR = "data-inline-styles";

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

	private CssInlinerConstants() {
		super();
	}
}
