package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Interface representing a Serde (serializer-deserializer), able to serialize and deserialize values of a given type
 *
 * @author Albert Troussard (330361)
 * @author Ménélik Nouvellon (328132)
 */
public interface Serde<C> {
    /**
     * <b>Serialize a object</b>
     *
     * @param c object to serialize
     * @return the string corresponding to the object to serialize
     */
    String serialize(C c);

    /**
     * <b>Deserialize a string</b>
     *
     * @param s String to serialize
     * @return object corresponding to given string
     */
    C deserialize(String s);

    /**
     * <b>Apply functions of serialization and deserialization</b>
     *
     * @param serialize   Function that is used to serialize
     * @param deserialize Function that is used to deserialize
     * @param <T>         Type of object
     * @return Serde corresponding to given function of serialization and deserialization
     */
    static <T> Serde<T> of(Function<T, String> serialize, Function<String, T> deserialize) {
        return new Serde<>() {
            @Override
            public String serialize(T t) {
                return serialize.apply(t);
            }

            @Override
            public T deserialize(String s) {
                return deserialize.apply(s);
            }
        };

    }

    /**
     * <b>Returning Serde that can serialize a given element of a list or deserialize a string of a <i>list</i>'s element</b> //PAS SUR DE CETTE EXPLICATION
     *
     * @param list list of all values in an enumerated set of values
     * @param <T>  type of object
     * @return Serde matching a given element of the <i>list</i>
     */
    static <T> Serde<T> oneOf(List<T> list) {
        return of(t -> t == null ? "" : String.valueOf(list.indexOf(t)),
                s -> s.equals("") ? null : list.get(Integer.parseInt(s)));
    }

    /**
     * <b>Returning a Serde capable of (de) serializing lists of values (de) serialized by the given serde </b>
     *
     * @param serde Serde
     * @param c     separator's character
     * @param <T>   type of object
     * @return returning a Serde capable of (de) serializing lists of values (de) serialized by the given serde
     */
    static <T> Serde<List<T>> listOf(Serde<T> serde, char c) {
        return of(
                t -> {
                    //replace elements of t with their serialization
                    List<String> aux = t.stream()
                            .map(serde::serialize)
                            .collect(Collectors.toList());

                    return t == null ? "" : String.join(String.valueOf(c), aux);
                },
                s -> {
                    //split the String with the given separator then replace each part with his deserialization
                    return s.equals("") ? List.of() : List.of(s.split(Pattern.quote(String.valueOf(c)), -1))
                            .stream()
                            .map(serde::deserialize)
                            .collect(Collectors.toList());
                });

    }


    /**
     * <b>Returning a Serde capable of (de) serializing lists of values (de) serialized by the given serde </b>
     *
     * @param serde Serde
     * @param c     separator's character
     * @param <T>   type of object
     * @return returning a Serde capable of (de) serializing lists of values (de) serialized by the given serde
     */
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, char c) {
        return of(t -> {
            //replace elements of t with their serialization
            List<String> aux = t.stream()
                    .map(serde::serialize)
                    .collect(Collectors.toList());

            return String.join(String.valueOf(c), aux);
        }, s -> {
            //split the String with the given separator then replace each part with his deserialization
            return s.equals("") ? SortedBag.of() : SortedBag.of(List.of(s.split(Pattern.quote(String.valueOf(c)), -1))
                    .stream()
                    .map(serde::deserialize)
                    .collect(Collectors.toList()));
        });
    }
}
