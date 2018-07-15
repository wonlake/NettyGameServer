import codecs
import os

cs_dir = "cs_output"
java_dir = "java_output"

if not os.path.exists(cs_dir):
	os.mkdir(cs_dir)

if not os.path.exists(java_dir):
	os.mkdir(java_dir)
	
cur_path = os.getcwd()
proto_path = cur_path + r"\Protos"

os.chdir(cs_dir)
protos = ""
with os.popen(r"dir /B " + proto_path +  r"\*.proto") as pipe:
    for l in pipe.readlines():
        protos += " " + l.strip()
    protos = protos + " --include_imports " + "--proto_path=" + proto_path

print(protos)
os.system(r"..\tools\ProtoGen.exe " + protos)
os.chdir(cur_path)

protos = ""
with os.popen(r"dir /B " + proto_path +  r"\*.proto") as pipe:
    for l in pipe.readlines():
        #protos += " " + proto_path + "\\" + l.strip()
        protos += " " + proto_path + "\\" + l.strip()
    protos = protos + " --java_out=" + java_dir + " -I" + proto_path

print(protos)
os.system(r"tools\protoc.exe " + protos)