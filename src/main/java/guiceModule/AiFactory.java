package guiceModule;

import com.google.inject.assistedinject.Assisted;
import computerAi.MiniMaxAlgoAi;
import entities.GameBoard;

public interface AiFactory {
    MiniMaxAlgoAi create(@Assisted GameBoard currentBoard);
}
