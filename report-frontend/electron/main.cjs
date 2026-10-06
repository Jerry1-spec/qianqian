// Electron 主进程：创建桌面窗口并加载前端页面
const { app, BrowserWindow, Menu, shell } = require('electron')
const path = require('path')

let mainWindow = null

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1280,
    height: 800,
    minWidth: 1024,
    minHeight: 700,
    title: '研究生周报助手',
    autoHideMenuBar: true,
    webPreferences: {
      contextIsolation: true,
      nodeIntegration: false,
    },
  })

  // 加载构建后的前端页面（file 协议）
  mainWindow.loadFile(path.join(__dirname, '..', 'dist', 'index.html'))

  // 外链用系统浏览器打开
  mainWindow.webContents.setWindowOpenHandler(({ url }) => {
    shell.openExternal(url)
    return { action: 'deny' }
  })

  mainWindow.on('closed', () => {
    mainWindow = null
  })
}

// 隐藏菜单栏（Windows/Linux）
Menu.setApplicationMenu(null)

app.whenReady().then(() => {
  createWindow()

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) createWindow()
  })
})

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') app.quit()
})
