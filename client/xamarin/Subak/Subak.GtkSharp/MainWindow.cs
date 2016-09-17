using System;
using Gtk;
using RestSharp;
using Subak.GtkSharp;

public partial class MainWindow : Gtk.Window
{
	public MainWindow() : base(Gtk.WindowType.Toplevel)
	{
		Build();
	}

	protected void OnDeleteEvent(object sender, DeleteEventArgs a)
	{
		Application.Quit();
		a.RetVal = true;
	}

	protected void OnButton1Clicked(object sender, EventArgs e)
	{
		Console.WriteLine(entry1.Text);
		var child = new TreeView();
		var model = new TreeStore(typeof(string), typeof(string));
		for (int i = 0; i < 5; i++)
		{
			var iter = model.AppendValues("Demo " + i, "Data " + i);
		}
		child.Model = model;
		child.AppendColumn("Demo", new CellRendererText(), "text", 0);
		child.AppendColumn("Data", new CellRendererText(), "text", 1);
		notebook2.AppendPage(child, new Label(entry1.Text));
		child.Show();
	}

	protected void OnOptionsActionActivated(object sender, EventArgs e)
	{
		var dialog = new SettingsWindow();
		dialog.Modal = true;
		dialog.ShowAll();
	}

	protected void OnReloadEnginesActionActivated(object sender, EventArgs e)
	{
		Console.WriteLine("load engines");
		var settings = SettingsSerializer.Load();
		var client = new RestClient();
		client.BaseUrl = new Uri(settings.Url);
		var request = new RestRequest();
		request.RequestFormat = DataFormat.Json;
		request.Resource = "/api/engines";
		var response = client.Execute<RestSharp.JsonArray>(request);
		Console.WriteLine(response);

	}
}
