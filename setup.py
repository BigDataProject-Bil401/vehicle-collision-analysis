import zipfile

zipdata = zipfile.ZipFile('./data/MVCInitialDataset.zip')
zipinfos = zipdata.infolist()

for zipinfo in zipinfos:
    zipinfo.filename = './data/MVCFinalDataset.csv'
    zipdata.extract(zipinfo)
