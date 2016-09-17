using System;
using System.IO;
using Newtonsoft.Json;

namespace Subak.GtkSharp
{
	public static class SettingsSerializer
	{
		internal static Settings Load()
		{
			var js = new JsonSerializer();
			var settings = new Settings();
			using (var sr = new StreamReader(System.IO.Path.Combine(
				System.Environment.GetFolderPath(System.Environment.SpecialFolder.Personal), "subak.config.json")))
			{
				settings = JsonConvert.DeserializeObject<Settings>(sr.ReadToEnd());
				Console.WriteLine(settings.Url);
			}

			return settings;
		}
	}
}