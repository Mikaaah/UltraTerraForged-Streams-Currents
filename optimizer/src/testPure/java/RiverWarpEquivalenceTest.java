import java.util.Random;
public final class RiverWarpEquivalenceTest {
    record Cell(double output){ Cell add(double a){return new Cell(output+a);} }
    static final class Net {
        float x,z,r;
        Net(float x,float z,float r){this.x=x;this.z=z;this.r=r;}
        boolean contains(float x,float z){return (x-this.x)*(x-this.x)+(z-this.z)*(z-this.z)<r*r;}
        Cell carve(Cell c,float rx,float rz,float lx,float lz){return c.add(rx*.001+rz*.001+lx*.01+lz*.01);}
    }
    static float lx(float rx,float rz){return (float)Math.sin(rx*.04+rz*.005);}
    static float lz(float rx,float rz){return (float)Math.cos(rx*.005-rz*.03);}
    static Cell original(Net[] nets,float x,float z) {
        float rx=x*.87f+z*.04f,rz=z*1.01f-x*.03f;
        float a=lx(rx,rz),b=lz(rx,rz);
        Cell c=new Cell(0);
        for(Net n:nets)if(n.contains(rx,rz))c=n.carve(c,rx,rz,a,b);
        return c;
    }
    static Cell optimized(Net[] nets,float x,float z) {
        float rx=x*.87f+z*.04f,rz=z*1.01f-x*.03f;
        int first=0;while(first<nets.length&&!nets[first].contains(rx,rz))first++;
        if(first==nets.length)return new Cell(0);
        float a=lx(rx,rz),b=lz(rx,rz);
        Cell c=new Cell(0);
        for(int i=first;i<nets.length;i++)if(nets[i].contains(rx,rz))c=nets[i].carve(c,rx,rz,a,b);
        return c;
    }
    public static void main(String[] unused) {
        Random r=new Random(1701);int count=0;
        for(int j=0;j<200;j++){
            Net[] nets=new Net[r.nextInt(8)];
            for(int k=0;k<nets.length;k++)nets[k]=new Net(r.nextFloat()*800-400,r.nextFloat()*800-400,r.nextFloat()*90+5);
            for(int i=0;i<1000;i++){
                float x=r.nextFloat()*1000-500,z=r.nextFloat()*1000-500;
                if(Double.doubleToLongBits(original(nets,x,z).output())!=Double.doubleToLongBits(optimized(nets,x,z).output()))throw new AssertionError("terrain mismatch");
                count++;
            }
        }
        System.out.println("PASS: "+count+" deterministic river-network control-flow comparisons.");
    }
}
