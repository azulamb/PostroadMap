package net.azulite.postroadmap;

/**
 * @author Hiroki
 * @version 1.0.0
 */

// TODO: max tile.

import java.util.Random;

/**
 * Create Postroad map.
 */
public class PostroadMap
{
	private char[][] map;
	private Random rnd = null;
	private int [] x = { 0, 0 }, y = { 0, 0 };
	private int tile;
	private float rate = 0.8f;
	private char tile_none  = 0;
	private char tile_route = 1;
	private char tile_wall  = 2;
	private int sentinel = 0;

	/**
	 * create PostroadMap
	 * @parsm w map width.
	 * @param h map height.
	 */
	public PostroadMap( int w, int h )
	{
		setMapSize( w, h );
	}
	/**
	 * Reset map size.
	 * @parsm w map width.
	 * @param h map height.
	 */
	public PostroadMap setMapSize( int w, int h )
	{
		map = new char[ h + 2 * sentinel ][ w + 2 * sentinel  ];
		return this;
	}
	/**
	 * Set map tile njber.
	 * @parsm tile_none no route number.(default)
	 * @param tile_route route number.(route)
	 * @param tile_wall sentinel wall.(not til_none, tile-route)
	 */
	public PostroadMap setTile( char tile_none, char tile_route, char tile_wall )
	{
		this.tile_none  = tile_none;
		this.tile_route = tile_route;
		this.tile_wall  = tile_wall;
		return this;
	}
	/**
	 * Set Random object.
	 * @param rnd Random.
	 */
	public PostroadMap setRandom( Random rnd )
	{
		this.rnd = rnd;
		return this;
	}
	/**
	 * Set sentinel.
	 * Map size + 2 & beginX, beginY, endX, endY + 1.
	 * @param enable tue = sentinel enble.
	 */
	public PostroadMap setSentinel( boolean enable )
	{
		if ( sentinel == 0 && enable )
		{
			// Off->On
			sentinel = 1;
			if( map != null )
			{
				setMapSize( map[ 0 ].length, map.length );
			}
		} else if ( sentinel == 1 && enable == false )
		{
			// On->Off
			sentinel = 0;
		}
		return this;
	}
	/**
	 * Set route arte.
	 * @param rate map tiles < route count * rate.(default 0.8)
	 */
	public PostroadMap setRate( float rate )
	{
		this.rate = rate;
		return this;
	}
	/**
	 * Generate postroad map.
	 */
	public PostroadMap generate()
	{
		if ( rnd == null ) { rnd =  new Random(); }
		do
		{
			init();
		} while( create( -1, -1 ) );
		return this;
	}
	/**
	 * Generate postroad map.
	 * @psram x Begin x.
	 * @param y Begin y.
	 */
	public PostroadMap generate( int x, int y )
	{
		if ( rnd == null ) { rnd =  new Random(); }
		do
		{
			init();
		} while( create( x, y ) );
		return this;
	}
	/**
	 * Get postrad map.
	 */
	public char[][] get(){ return map; }
	/**
	 * Get begin x.
	 * Position +1 when enable sentinel.
	 */
	public int beginX(){ return x[ 0 ]; }
	/**
	 * Get bbegin y.
	 * Position +1 when enable sentinel.
	 */
	public int beginY(){ return y[ 0 ]; }
	/**
	 * Get goal x.
	 * Position +1 when enable sentinel.
	 */
	public int endX(){ return x[ 1 ]; }
	/**
	 * Get goal y.
	 * Position +1 when enable sentinel.
	 */
	public int endY(){ return y[ 1 ]; }
	/**
	 * Get route tile count.
	 */
	public int tile(){ return tile; }

	private void init()
	{
		int x, y;
		int w, h;
		h = map.length - 2 * sentinel;
		w = map[ 0 ].length - 2 * sentinel;
		for ( y = 0 ; y < h ; ++y ){for ( x = 0 ; x < w ; ++x ){ map[ y + sentinel ][ x + sentinel ] = tile_none; } }
		if ( sentinel == 1 )
		{
			h += 2;
			w += 1;
			for ( y = 0 ; y < h ; ++y )
			{
				map[ y ][ 0 ] = tile_wall;
				map[ y ][ w ] = tile_wall;
			}
			w += 1;
			h -= 1;
			for ( x = 0 ; x < w ; ++x )
			{
				map[ 0 ][ x ] = tile_wall;
				map[ h ][ x ] = tile_wall;
			}
		}
	}
	private boolean create( int sx, int sy )
	{
		int w, h;
		boolean fix = false;

		h = map.length - 2 * sentinel;
		w = map[ 0 ].length - 2 * sentinel;

		if ( sx < 0 || sy < 0 )
		{
			x[ 0 ] = sentinel + w / 2 - 1;
			y[ 0 ] = sentinel + h / 2;
			x[ 1 ] = sentinel + w / 2;
			y[ 1 ] = sentinel + h / 2;
		} else
		{
			x[ 0 ] = sentinel + sx;
			y[ 0 ] = sentinel + sy;
			x[ 1 ] = ( x[ 0 ] + 1 < w ) ? x[ 0 ] + 1 : x[ 0 ] - 1;
			y[ 1 ] = sy;
			fix = true;
		}

		int target = 1;
		tile = 0;
		while( true )
		{
			if ( fix )
			{
				if ( check( x[ 1 ], y[ 1 ] ) == false ) { break; }
			} else
			{
				target = rnd.nextInt( 2 );
				if ( check( x[ target ], y[ target ] ) == false )
				{
					target = ( target + 1 ) % 2;
					if ( check( x[ target ], y[ target ] ) == false )
					{
						break;
					}
				}
			}

			++tile;
			switch ( move( x[ target ], y[ target ] ) )
			{
			case 0: // up
				--y[ target ];
				break;
			case 1: // right
				++x[ target ];
				break;
			case 2: // down
				++y[ target ];
				break;
			case 3: // left
				--x[ target ];
				break;
			}
		}
		return tile < w * h * rate;
	}
	private boolean check( int x, int y )
	{
		if ( 0 < x                   && map[ y ][ x - 1 ] == tile_none ){ return true; }
		if ( 0 < y                   && map[ y - 1 ][ x ] == tile_none ){ return true; }
		if ( y + 1 < map.length      && map[ y + 1 ][ x ] == tile_none ){ return true; }
		if ( x + 1 < map[ 0 ].length && map[ y ][ x + 1 ] == tile_none ){ return true; }
		return false;
	}
	private int move( int x, int y )
	{
		int a;
		int s, t;
		while( true )
		{
			a = rnd.nextInt( 4 );
			s = x + (a%2)*(2-a);
			if ( s < 0 || map[ 0 ].length <= s ){ continue; }
			t = y + ((a+1)%2)*(a-1);
			if ( t < 0 || map.length <= t ){ continue; }
			if ( map[ t ][ s ] == tile_none ){ break; }
		}
		map[ t ][ s ] = tile_route;
		return a;
	}
}
