using Xamarin.Forms;

namespace Subak
{
	public partial class SubakPage : ContentPage
	{
		public SubakPage()
		{
			InitializeComponent();
			listView.ItemsSource = Data.TrackList;
		}
	}
}

