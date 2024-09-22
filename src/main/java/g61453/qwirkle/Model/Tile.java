package g61453.qwirkle.Model;
import java.io.Serializable;

/*
* Tile represent a tile of the Qwirkle game
 */
public record Tile(Color color, Shape shape) implements Serializable {
    public boolean equals(Object obj){
        return this == obj;
    }
}
