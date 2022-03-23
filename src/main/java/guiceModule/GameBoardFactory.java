package guiceModule;

import com.google.inject.assistedinject.Assisted;
import entities.GameBoard;

public interface GameBoardFactory {
    GameBoard create(@Assisted("size") int size, @Assisted("width") int width, @Assisted("height") int height);
}
