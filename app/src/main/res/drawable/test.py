import os



for file_name in os.listdir('.'):
	os.rename(file_name, 'cut' + file_name)
