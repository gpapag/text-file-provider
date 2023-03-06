package project.textprovider.exceptions;

public class TextProviderException extends Exception {
  private static final long serialVersionUID = 42424242L;

  public TextProviderException(final String message) {
    super(message);
  }

  public TextProviderException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
