package dev.pratul;

public class UserServiceException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6137261322610086049L;
	
	public UserServiceException(String message)
    {
          super(message);
    }

}
