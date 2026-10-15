package nl.townsandkingdoms.utfstreams;

/** Small pure rules shared with the focused regression checks. */
public final class FlowRules {
    private FlowRules() {}
    public static boolean uphill(double before, double here, double after) {
        return here > before + 0.5 || after > here + 0.5;
    }
    public static int waterY(float normalized, int scale) {
        return (int)Math.floor((double)normalized * scale + 0.001);
    }
    public static boolean usableVector(double dx, double dz) {
        return Double.isFinite(dx) && Double.isFinite(dz) && dx * dx + dz * dz > 0.01;
    }
}
