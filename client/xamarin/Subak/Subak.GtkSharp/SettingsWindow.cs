using System;
using System.IO;
using Newtonsoft.Json;

namespace Subak.GtkSharp
{
	public partial class SettingsWindow : Gtk.Window
	{
		public SettingsWindow() :
				base(Gtk.WindowType.Toplevel)
		{
			this.Build();

			Console.WriteLine(System.Environment.GetFolderPath(System.Environment.SpecialFolder.Personal));

			var settings = SettingsSerializer.Load();
			entryServerUrl.Text = settings.Url;
		}

		protected void OnButtonOkClicked(object sender, EventArgs e)
		{
			this.Destroy();
		}

		protected void OnButtonCancelClicked(object sender, EventArgs e)
		{
			this.Destroy();
		}
	}
}
