package name.ncg777.computing.graphics;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class SpriteMat {
    private Mat image;
    private double x, y;      // Top-left position
    private double angle;     // Rotation in degrees
    private double alpha;     // 0.0 = transparent, 1.0 = opaque

    public SpriteMat(Mat image) {
        this.image = image.clone();
        this.x = 0;
        this.y = 0;
        this.angle = 0;
        this.alpha = 1.0;
    }

    public void setPosition(double x, double y) { this.x = x; this.y = y; }
    public void setRotation(double angle) { this.angle = angle; }
    public void setAlpha(double alpha) { this.alpha = Math.max(0, Math.min(1, alpha)); }
    public void setImage(Mat newImage) { this.image = newImage.clone(); }

    // Draw sprite onto destination Mat
    public void draw(Mat dest) {
        // 1. Rotate image if needed
        Mat transformed = image;
        if (angle != 0) {
            Point center = new Point(image.cols()/2.0, image.rows()/2.0);
            Mat rotMat = Imgproc.getRotationMatrix2D(center, angle, 1.0);
            Imgproc.warpAffine(image, transformed = new Mat(), rotMat, image.size(), Imgproc.INTER_LINEAR, Core.BORDER_TRANSPARENT, Scalar.all(0));
        }

        // 2. Determine ROI in dest
        int w = transformed.cols();
        int h = transformed.rows();
        int destX = (int) Math.round(x);
        int destY = (int) Math.round(y);

        // Clip if out of bounds
        int roiW = Math.min(w, dest.cols() - destX);
        int roiH = Math.min(h, dest.rows() - destY);
        if (destX < 0 || destY < 0 || roiW <= 0 || roiH <= 0) return; // completely out of bounds

        // 3. Alpha blend transformed image into dest Mat
        Mat destROI = dest.submat(destY, destY + roiH, destX, destX + roiW);
        Mat spriteROI = transformed.submat(0, roiH, 0, roiW);

        if (spriteROI.channels() == 4) { // Has alpha channel
            for (int row = 0; row < roiH; row++) {
                for (int col = 0; col < roiW; col++) {
                    double[] spritePx = spriteROI.get(row, col);
                    double spriteAlpha = (spritePx[3] / 255.0) * alpha;
                    double[] destPx = destROI.get(row, col);
                    for (int c = 0; c < 3; c++) {
                        destPx[c] = spritePx[c] * spriteAlpha + destPx[c] * (1 - spriteAlpha);
                    }
                    destROI.put(row, col, destPx);
                }
            }
        } else { // BGR only; use global alpha
            for (int row = 0; row < roiH; row++) {
                for (int col = 0; col < roiW; col++) {
                    double[] spritePx = spriteROI.get(row, col);
                    double[] destPx = destROI.get(row, col);
                    for (int c = 0; c < 3; c++) {
                        destPx[c] = spritePx[c] * alpha + destPx[c] * (1 - alpha);
                    }
                    destROI.put(row, col, destPx);
                }
            }
        }
    }
}
