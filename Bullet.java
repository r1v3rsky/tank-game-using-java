import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.List;
class Bullet {
    double x, y;
    double dx, dy;
    double angle; // 💡 mermiye ait açı
    ImageView imageView;
    boolean addedToScene = false;
    static final double SPEED = 3;
    static final List<Bullet> bullets = new ArrayList<>();

    Bullet(double startX, double startY, int dirX, int dirY, double angle) {
        this.x = startX+10;
        this.y = startY+10;
        this.dx = dirX * SPEED;
        this.dy = dirY * SPEED;
        this.angle = angle;

        Image image = new Image("file:assets/bullet.png");
        this.imageView = new ImageView(image);
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
        imageView.setRotate(angle);
    }
    void update() {
        x += dx;
        y += dy;
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
        imageView.setRotate(angle);
    }
}
