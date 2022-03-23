package guiceModule;

import com.google.inject.assistedinject.Assisted;
import frames.GameGraphicalComponent;

public interface GraphicalComponentFactory {
    GameGraphicalComponent create(@Assisted("width") int width, @Assisted("height") int height, @Assisted("size") int size, @Assisted("gameType") int gameType);
}
