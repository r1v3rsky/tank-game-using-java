import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.Group;
import javafx.util.Duration;

import java.util.*;


public class TankSystem extends Application {
    Group root = new Group();
    Group world = new Group(root);
    Group scores = new Group();
    Canvas canvas = new Canvas(1600, 1200); // big map
    Scene scene = new Scene(new Group(world, scores), 800, 600); // fix map
    Group pauseMenu = new Group();

    double playerX = 400;
    double playerY = 1000;
    int directionX = 0;
    int directionY = -1;
    int angle = 270;
    Random random = new Random();
    Image tank1 = new Image("file:assets/yellowTank1.png");
    Image tank2 = new Image("file:assets/yellowTank2.png");
    ImageView tankView = new ImageView(tank1); // Başta ilk görüntü
    Image explosion = new Image("file:assets/explosion.png");
    ImageView explosioN = new ImageView(explosion);
    Image smallExp = new Image("file:assets/smallExplosion.png");
    List<ImageView> walls = new ArrayList<>();
    Timeline timeline = new Timeline();
    Set<KeyCode> pressedKeys = new HashSet<>();
    Text scoreText = new Text();
    boolean isPaused = false;
    int score = 0;
    int lives = 3;
    boolean gameOver = false;


    @Override
    public void start(Stage stage) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);
        updateScoreText();  // first values of lives and score
        scoreText.setLayoutX(25);
        scoreText.setLayoutY(50);
        scores.getChildren().add(scoreText);
        root.getChildren().add(tankView);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 1600, 1200);
        timeline.setCycleCount(Timeline.INDEFINITE);  // for work to infinity

        runRandomAction(timeline, walls, root);

        scene.setOnKeyPressed(e -> {
            pressedKeys.add(e.getCode());

            if (e.getCode() == KeyCode.X && !gameOver && !isPaused) {
                Bullet bullet = new Bullet(playerX, playerY, directionX, directionY, angle);
                Bullet.bullets.add(bullet);
            }

            if (e.getCode() == KeyCode.P && !gameOver) {
                pauseMenu();
            }

            if (e.getCode() == KeyCode.R && (gameOver || isPaused)) {
                gameOver = false;
                isPaused = false;
                root.getChildren().removeIf(node -> node instanceof Text);
                lives = 3;
                score = 0;
                resetGame();
            }

            if (e.getCode() == KeyCode.ESCAPE && (gameOver || isPaused)) {
                System.exit(0);
            }
        });

        scene.setOnKeyReleased(e -> {
            pressedKeys.remove(e.getCode());
        });

        createWall();

        new javafx.animation.AnimationTimer() {
            public void handle(long now) {
                if (!isPaused) {
                    double offsetX = playerX - 400;
                    double offsetY = playerY - 300;

                    double maxOffsetX = 1600 - 800; //width
                    double maxOffsetY = 1200 - 600; // height

                    offsetX = Math.max(0, Math.min(offsetX, maxOffsetX));
                    offsetY = Math.max(0, Math.min(offsetY, maxOffsetY));
                    world.setLayoutX(-offsetX);
                    world.setLayoutY(-offsetY);

                    if (!gameOver && !isPaused) {
                        if (pressedKeys.contains(KeyCode.UP)) {
                            playerY -= 1.5;
                            directionX = 0;
                            directionY = -1;
                            angle = 270;
                        }
                        if (pressedKeys.contains(KeyCode.DOWN)) {
                            playerY += 1.5;
                            directionX = 0;
                            directionY = 1;
                            angle = 90;
                        }
                        if (pressedKeys.contains(KeyCode.LEFT)) {
                            playerX -= 1.5;
                            directionX = -1;
                            directionY = 0;
                            angle = 180;
                        }
                        if (pressedKeys.contains(KeyCode.RIGHT)) {
                            playerX += 1.5;
                            directionX = 1;
                            directionY = 0;
                            angle = 0;
                        }
                    }


                    long frame = (now / 15_000_000) % 2;
                    if (gameOver) return;
                    if (frame == 0) { // this is for animation
                        tankView.setImage(tank1);
                    } else {
                        tankView.setImage(tank2);
                    }
                    tankView.setLayoutX(playerX);
                    tankView.setLayoutY(playerY);
                    tankView.setRotate(angle);
                    bulletControl(Bullet.bullets, root);
                    bulletControl(Enemy.enemyBullets, root);

                    for (ImageView wall : walls) {  // for not touching walls
                        if (tankView.getBoundsInParent().intersects(wall.getBoundsInParent())) {
                            playerX -= directionX * 1.5;
                            playerY -= directionY * 1.5;
                            break;
                        }
                    }
                }
            }
        }.start();
        stage.setTitle("Tank 2025");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void runRandomAction(Timeline timeline, List<ImageView> walls, Group root) { // this is for create enemies at random time
        int delaySeconds = 1 + random.nextInt(5);
        timeline.stop();
        timeline.getKeyFrames().setAll(
                new KeyFrame(Duration.seconds(delaySeconds), event -> {
                    new Enemy(800 + random.nextInt(750), 50 + random.nextInt(500), walls, root);
                    runRandomAction(timeline, walls, root);  // call yourself
                })
        );
        timeline.play();
    }

    private void bulletControl(List<Bullet> bullets, Group root) {
        List<Bullet> bulletsToRemove = new ArrayList<>();
        if (gameOver) return;

        for (Bullet bullet : new ArrayList<>(bullets)) {
            bullet.update();

            if (!bullet.addedToScene) {
                root.getChildren().add(bullet.imageView);
                bullet.addedToScene = true;
            }
            for (ImageView wall : walls) { // for not touching walls
                if (bullet.imageView.getBoundsInParent().intersects(wall.getBoundsInParent())) {
                    bulletsToRemove.add(bullet);
                    root.getChildren().remove(bullet.imageView);

                    ImageView smallExplosion = new ImageView(smallExp);
                    smallExplosion.setLayoutX(bullet.x);
                    smallExplosion.setLayoutY(bullet.y);
                    root.getChildren().add(smallExplosion);

                    Timeline removeExplosion = new Timeline(new KeyFrame(Duration.seconds(0.5), evt -> {
                        root.getChildren().remove(smallExplosion); // remove explosion
                    }));
                    removeExplosion.play();
                    break;
                }
            }
            // check players bullets for collision with enemy
            if (bullets == Bullet.bullets) {
                for (Enemy enemy : new ArrayList<>(Enemy.activeEnemies)) {
                    if (bullet.imageView.getBoundsInParent().intersects(enemy.tankView.getBoundsInParent())) {
                        bulletsToRemove.add(bullet);
                        root.getChildren().remove(bullet.imageView);

                        root.getChildren().remove(enemy.tankView);
                        Enemy.activeEnemies.remove(enemy);
                        enemy.stop();

                        score += 100;
                        updateScoreText();

                        ImageView explosionView = new ImageView(explosion);

                        explosionView.setLayoutX(enemy.tankView.getLayoutX());
                        explosionView.setLayoutY(enemy.tankView.getLayoutY());
                        root.getChildren().add(explosionView);

                        Timeline removeExplosion = new Timeline(new KeyFrame(Duration.seconds(0.5), evt -> {
                            root.getChildren().remove(explosionView);
                        }));
                        removeExplosion.play();
                    }
                }
            }
            // check enemy's bullets for collision with player's tank
            if (bullets.equals(Enemy.enemyBullets)) {
                if (bullet.imageView.getBoundsInParent().intersects(tankView.getBoundsInParent())) {
                    bulletsToRemove.add(bullet);
                    root.getChildren().remove(bullet.imageView);

                    lives--;
                    updateScoreText();
                    for (Enemy enemy : Enemy.activeEnemies) {
                        enemy.stop(); // Hareket ve ateş etmeyi durdur
                    }
                    explosioN.setLayoutX(tankView.getLayoutX());
                    explosioN.setLayoutY(tankView.getLayoutY());
                    root.getChildren().remove(tankView);
                    root.getChildren().add(explosioN);

                    if (lives <= 0 && !gameOver) {
                        gameOver = true;

                        Text gameOverText = new Text("GAME OVER!\nYour score is: " + score + "\n\nPress R to restart!");
                        gameOverText.setFill(Color.RED);
                        gameOverText.setLayoutY(tankView.getLayoutY() - 100);
                        gameOverText.setLayoutX(tankView.getLayoutX() - 150);
                        gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
                        root.getChildren().add(gameOverText);

                        Bullet.bullets.clear();
                        Enemy.enemyBullets.clear();
                        timeline.stop();
                    } else {
                        bullets.removeAll(bulletsToRemove);
                        PauseTransition pause = new PauseTransition(Duration.seconds(1));
                        pause.setOnFinished(e -> resetGame());
                        pause.play();

                    }

                }
            }
        }

        bullets.removeAll(bulletsToRemove);
    }

    private void updateScoreText() {
        lives = Math.max(0, lives);
        scoreText.setStyle("-fx-font-size: 20px; -fx-fill: white;");
        scoreText.setText("Score: " + score + "\nLives: " + lives);
    }

    private void resetGame() {
        isPaused = false;
        root.getChildren().remove(pauseMenu);
        pauseMenu.getChildren().clear();

        playerX = 400;
        playerY = 1000;
        directionX = 0;
        directionY = -1;
        angle = 270;

        // clear scene
        for (Bullet bullet : Bullet.bullets) {
            root.getChildren().remove(bullet.imageView);
        }
        for (Bullet bullet : Enemy.enemyBullets) {
            root.getChildren().remove(bullet.imageView);
        }
        for (Enemy enemy : Enemy.activeEnemies) {
            enemy.stop();
            root.getChildren().remove(enemy.tankView);
        }

        Bullet.bullets.clear();
        Enemy.enemyBullets.clear();
        Enemy.activeEnemies.clear();

        root.getChildren().clear();
        walls.clear();

        root.getChildren().add(canvas);
        root.getChildren().add(tankView);
        updateScoreText();

        createWall();

        tankView.setLayoutX(playerX);
        tankView.setLayoutY(playerY);
        tankView.setRotate(angle);

        timeline.stop();
        runRandomAction(timeline, walls, root);
    }

    private void pauseMenu() {
        isPaused = !isPaused;
        if (isPaused) {
            timeline.pause();

            Text pauseText = new Text("PAUSED\nR - Restart\nESC - Exit");
            pauseText.setFill(Color.RED);
            pauseText.setX(playerX - 150);
            pauseText.setY(playerY - 150);
            pauseText.setStyle("-fx-font-size: 28px;");
            pauseMenu.getChildren().add(pauseText);
            root.getChildren().add(pauseMenu);
        } else {
            timeline.play();
            root.getChildren().remove(pauseMenu);
            pauseMenu.getChildren().clear();
        }
    }

    public void createWall() {
        for (int j = 1; j < 6; j++) {
            for (int i = 0; i < 10; i++) {
                ImageView wall = new ImageView(new Image("file:assets/wall.png"));
                wall.setX(100);
                wall.setY(j * 100 + i * 15); // her duvar 32 piksel yükseklik varsayalım
                walls.add(wall);
                root.getChildren().add(wall);
            }
            for (int i = 0; i < 10; i++) {
                ImageView wall = new ImageView(new Image("file:assets/wall.png"));
                wall.setLayoutX(j * 100);
                wall.setLayoutY(200 + i * 15);
                walls.add(wall);
                root.getChildren().add(wall);
            }
            for (int i = 1; i < 10; i++) {
                ImageView wall = new ImageView(new Image("file:assets/wall.png"));
                wall.setLayoutX(j * 300 + i * 15);
                wall.setLayoutY(400 + 2 * 15);
                walls.add(wall);
                root.getChildren().add(wall);
            }
        }
        int wallSize = 15;

        for (int x = 0; x < 1600; x += wallSize) {
            ImageView wall = new ImageView(new Image("file:assets/wall.png"));
            wall.setLayoutX(x);
            wall.setLayoutY(0);
            walls.add(wall);
            root.getChildren().add(wall);
        }

        for (int x = 0; x < 1600; x += wallSize) {
            ImageView wall = new ImageView(new Image("file:assets/wall.png"));
            wall.setLayoutX(x);
            wall.setLayoutY(1200 - wallSize);
            walls.add(wall);
            root.getChildren().add(wall);
        }

        for (int y = 0; y < 1200; y += wallSize) {
            ImageView wall = new ImageView(new Image("file:assets/wall.png"));
            wall.setLayoutX(0);
            wall.setLayoutY(y);
            walls.add(wall);
            root.getChildren().add(wall);
        }

        for (int y = 0; y < 1200; y += wallSize) {
            ImageView wall = new ImageView(new Image("file:assets/wall.png"));
            wall.setLayoutX(1600 - wallSize);
            wall.setLayoutY(y);
            walls.add(wall);
            root.getChildren().add(wall);
        }
    }
}
