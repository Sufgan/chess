javac --release 8 -d bin src/com/sufgan/chess/*java src/com/sufgan/chess/pieces/*
jar -cmf manifest.mf chess.jar -C bin .