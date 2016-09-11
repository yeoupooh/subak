using System;
using System.Collections.ObjectModel;

namespace Subak
{
	public class Track
	{
		public string Title { get; set; }
		public string Artist { get; set; }
		public string File { get; set; }
	}

	public class Data
	{
		public Data()
		{
		}

		public static ObservableCollection<Track> TrackList = new ObservableCollection<Track>
		{
			new Track{ Title="Title1", Artist="Artist1", File="File1"},
			new Track{ Title="Title2", Artist="Artist2", File="File2"}
		};
	}
}
