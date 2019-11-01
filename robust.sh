#!/bin/sh

# 进入主项目目录
cd app

# 删除缓存文件
rm -rf robust

# 重新创建robust目录
mkdir robust

# 添加混淆mapping
cp build/outputs/mapping/release/mapping.txt robust

# 添加methodMap文件
cp build/outputs/robust/methodsMap.robust robust
