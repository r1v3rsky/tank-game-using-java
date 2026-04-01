import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemy {
    private double x, y;
    private int directionX = 0, directionY = 1; // Başlangıçta aşağı gidiyor
    int angle;
    private Timeline moveTimer, shootTimer;
    private Random random = new Random();
    public ImageView tankView;
    public static List<Bullet> enemyBullets = new ArrayList<>();
    private int stepsUntilTurn = 0;
    public static List<Enemy> activeEnemies = new ArrayList<>();
    private Timeline animationTimeline;
    Image enemyTank1 = new Image("file:assets/whiteTank1.png");
    Image enemyTank2 = new Image("file:assets/whiteTank2.png");



    // public static List<Enemy> allEnemies = new ArrayList<>();

    public Enemy(double startX, double startY, List<ImageView> walls, Group root) {
        this.x = startX;
        this.y = startY;
        this.tankView = new ImageView(enemyTank1);

        tankView.setLayoutX(x);
        tankView.setLayoutY(y);
        setupAnimation();
        root.getChildren().add(this.tankView);
        move(walls, root);
        activeEnemies.add(this);

    }

    public void move(List<ImageView> walls, Group root) {
        moveTimer = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> {
            // change direction
            if (stepsUntilTurn <= 0) {
                int dir = random.nextInt(4);
                switch (dir) {
                    case 0:
                        directionX = 0;
                        directionY = -1;
                        angle = 270;
                        break;
                    case 1:
                        directionX = 0;
                        directionY = 1;
                        angle = 90;
                        break;
                    case 2:
                        directionX = -1;
                        directionY = 0;
                        angle = 180;
                        break;
                    case 3:
                        directionX = 1;
                        directionY = 0;
                        angle = 0;
                        break;
                }
                stepsUntilTurn = 10 + random.nextInt(10);// same direction for 10-19 step
            } else {
                stepsUntilTurn--;
            }

            double newX = x + directionX * 5;
            double newY = y + directionY * 5;




            tankView.setLayoutX(newX);
            tankView.setLayoutY(newY);
            boolean collision = false;

            // for collision with walls
            for (ImageView wall : walls) {
                if (tankView.getBoundsInParent().intersects(wall.getBoundsInParent())) {
                    collision = true;
                    break;
                }
            }
            tankView.setLayoutX(x);
            tankView.setLayoutY(y);

            if (!collision) {
                x = newX;
                y = newY;
                tankView.setLayoutX(x);
                tankView.setLayoutY(y);
                tankView.setRotate(angle);
            } else {
                stepsUntilTurn = 0; // if collision change the route
            }
        }));
        moveTimer.setCycleCount(Timeline.INDEFINITE);
        moveTimer.play();
        // this is for shooting random time
        shootTimer = new Timeline(new KeyFrame(Duration.seconds(1.5 + random.nextDouble()), e -> {
            Bullet bullet = new Bullet(x, y, directionX, directionY, angle);
            enemyBullets.add(bullet);
        }));
        shootTimer.setCycleCount(Timeline.INDEFINITE);
        shootTimer.play();
    }
    public void stop() { // enemy stop
        if (moveTimer != null) {
            moveTimer.stop();
        }
        if (shootTimer != null) {
            shootTimer.stop();
        }
    }
    private void setupAnimation() {
        animationTimeline = new Timeline(
                new KeyFrame(Duration.millis(1), e -> animationImage())
        );
        animationTimeline.setCycleCount(Timeline.INDEFINITE);
        animationTimeline.play();
    }

    private void animationImage() {
        if (tankView.getImage() == enemyTank1) {
            tankView.setImage(enemyTank2);
        } else {
            tankView.setImage(enemyTank1);
        }
    }

}
