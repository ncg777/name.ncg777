package name.ncg777.computing.graphics;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class SpriteMat {
    private Mat image;
    private double cx, cy;   // Center coordinates
    private double angle;    // Rotation in degrees
    private double alpha;    // 0.0 = transparent, 1.0 = opaque
    private double scale;    // 1.0 = normal size

    public SpriteMat(Mat image) {
        this.image = image.clone();
        this.cx = 0;
        this.cy = 0;
        this.angle = 0;
        this.alpha = 1.0;
        this.scale = 1.0;
    }

    public void setCenter(double cx, double cy) { this.cx = cx; this.cy = cy; }
    public void setRotation(double angle) { this.angle = angle; }
    public void setAlpha(double alpha) { this.alpha = Math.max(0, Math.min(1, alpha)); }
    public void setScale(double scale) { this.scale = scale; }
    public void setImage(Mat newImage) { this.image = newImage.clone(); }

    public void draw(Mat dest) {
        // 1. Scale and rotate image about its center
        Size origSize = image.size();
        Point center = new Point(origSize.width / 2.0, origSize.height / 2.0);
        Mat rotMat = Imgproc.getRotationMatrix2D(center, angle, scale);

        // 2. Compute the bounding box after transformation
        Rect bbox = getTransformedBoundingBox(origSize, rotMat);

        // 3. Warp (rotate+scale) image
        Mat transformed = new Mat();
        Imgproc.warpAffine(image, transformed, rotMat, bbox.size(), Imgproc.INTER_LINEAR, Core.BORDER_TRANSPARENT, Scalar.all(0));

        // 4. Compute top-left for paste, so that (cx, cy) is the center of the transformed image
        int topLeftX = (int) Math.round(cx - transformed.cols() / 2.0);
        int topLeftY = (int) Math.round(cy - transformed.rows() / 2.0);

        // 5. Alpha blending
        blendTransformedSprite(transformed, dest, topLeftX, topLeftY);
    }

    // Helper: Compute bounding box for rotated+scaled image
    private Rect getTransformedBoundingBox(Size size, Mat rotMat) {
        // Four corners of the original image
        Point[] corners = new Point[] {
            new Point(0, 0),
            new Point(size.width, 0),
            new Point(size.width, size.height),
            new Point(0, size.height)
        };
        double[] m = new double[6];
        rotMat.get(0, 0, m);

        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        for (Point p : corners) {
            double x = m[0]*p.x + m[1]*p.y + m[2];
            double y = m[3]*p.x + m[4]*p.y + m[5];
            minX = Math.min(minX, x); minY = Math.min(minY, y);
            maxX = Math.max(maxX, x); maxY = Math.max(maxY, y);
        }
        int width  = (int)Math.ceil(maxX - minX);
        int height = (int)Math.ceil(maxY - minY);

        // Adjust rotMat to shift the image so the new top-left is at (0,0)
        rotMat.put(0, 2, m[2] - minX);
        rotMat.put(1, 2, m[5] - minY);

        return new Rect(0, 0, width, height);
    }

    // Helper: Alpha blend transformed sprite onto destination at (x, y)
    private void blendTransformedSprite(Mat sprite, Mat dest, int x, int y) {
        int w = sprite.cols();
        int h = sprite.rows();

        // Compute ROI in destination (clip if necessary)
        int destCols = dest.cols(), destRows = dest.rows();
        int srcX0 = 0, srcY0 = 0, dstX0 = x, dstY0 = y;

        if (dstX0 < 0) { srcX0 = -dstX0; w += dstX0; dstX0 = 0; }
        if (dstY0 < 0) { srcY0 = -dstY0; h += dstY0; dstY0 = 0; }
        if (dstX0 + w > destCols) w = destCols - dstX0;
        if (dstY0 + h > destRows) h = destRows - dstY0;
        if (w <= 0 || h <= 0) return; // completely out of bounds

        Mat destROI = dest.submat(dstY0, dstY0 + h, dstX0, dstX0 + w);
        Mat spriteROI = sprite.submat(srcY0, srcY0 + h, srcX0, srcX0 + w);

        if (spriteROI.channels() == 4) { // BGRA
            for (int row = 0; row < h; row++) {
                for (int col = 0; col < w; col++) {
                    double[] sPx = spriteROI.get(row, col);
                    double spriteAlpha = (sPx[3] / 255.0) * this.alpha;
                    double[] dPx = destROI.get(row, col);
                    for (int c = 0; c < 3; c++) {
                        dPx[c] = sPx[c] * spriteAlpha + dPx[c] * (1 - spriteAlpha);
                    }
                    destROI.put(row, col, dPx);
                }
            }
        } else { // BGR, use global alpha
            for (int row = 0; row < h; row++) {
                for (int col = 0; col < w; col++) {
                    double[] sPx = spriteROI.get(row, col);
                    double[] dPx = destROI.get(row, col);
                    for (int c = 0; c < 3; c++) {
                        dPx[c] = sPx[c] * this.alpha + dPx[c] * (1 - this.alpha);
                    }
                    destROI.put(row, col, dPx);
                }
            }
        }
    }
}
