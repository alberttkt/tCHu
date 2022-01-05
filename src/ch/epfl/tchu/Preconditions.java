package ch.epfl.tchu;

/**
 * @author Albert Troussard (330361)
 * @author Menelik Nouvellon (328132)
 */
public final class Preconditions {
    private Preconditions() {
    }

    /**
     * Method to throw IllegalArgumentException error if a statement is false
     *
     * @param shouldBeTrue
     * @throws IllegalArgumentException() if statement is false
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }

}

