import net.azulite.postroadmap.PostroadMap;

public class Main
{
	public static void main( String[] argv )
	{
		PostroadMap map = new PostroadMap( 5, 5 );
		//map.setSentinel( true ).generate( 2, 2 );
		map.generate();
		System.out.print( "S:" + map.beginX() + "," + map.beginY() + " G:" + map.endX() + "," + map.endY() + " T:" + map.tile() + "\n" );
		print( map.get() );
	}
	static void print( char[][] map )
	{
		int x, y;
		int w, h;
		h = map.length;
		w = map[ 0 ].length;
		for ( y = 0 ; y < h ; ++y ){for ( x = 0 ; x < w ; ++x ){
			System.out.print( map[ y ][ x ] == 0 ? " " : map[ y ][ x ] == 2 ? "#" : "+" );
		} System.out.print("\n"); }
	}
}
