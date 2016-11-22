import groovy.swing.SwingBuilder
import groovy.beans.Bindable
import static javax.swing.JFrame.EXIT_ON_CLOSE
import java.awt.*
java.util.Random
import javax.swing.BorderFactory
import javax.swing.border.Border
import javax.swing.JFrame

//initialize the swingbuilder
def builder = new SwingBuilder()

def colorlist = [Color.red, Color.blue, Color.pink, Color.green, Color.yellow, Color.cyan, Color.magenta, Color.orange]

//begin creating the gui
builder.edt {
  lookAndFeel 'nimbus'
  frame(title: 'Password Generator', size: [700, 250],
  show: true, locationRelativeTo: null,
  defaultCloseOperation: EXIT_ON_CLOSE) {
    borderLayout(vgap: 5)

    panel(constraints: BorderLayout.CENTER,
      border: compoundBorder([emptyBorder(10), titledBorder('Generate A Password:')])) {
        tableLayout {
          tr {
            td {
              label 'Special words in password:'
            }
            td {
              textField id: 'specialfield', columns: 20
            }
            td {
              button id: 'addspec', text: 'add', actionPerformed: {
                ArrayList temp = specarea.text.split("\n")
                Random ran = new Random()
                ready1 = true
                ready2 = true
                if(specialfield.text == '') {
                  specialfield.setBorder(BorderFactory.createLineBorder(colorlist[ran.nextInt(8)], 1))
                  ready1 = false
                }
                if(temp.size() > 3) {
                  maxlabel.setForeground(colorlist[ran.nextInt(8)])
                  ready2 = false
                }
                if(ready1 && ready2) {
                  specarea.append(specialfield.text + '\n')
                  specialfield.setText("")
                }
              }
            }
          }
          tr {
            td {
              scrollPane(id: 'scroll', preferredSize: new Dimension(200, 75)){
                textArea(
                  id: 'specarea', editable: false
                  )
                }
              }
            td {
                label id:'maxlabel', text: 'MAX: 4'
              }
          }
          tr {
              td {
                label 'Password:'
              }
              td {
                label id:'pwdlabel', text:''
              }
              td {
                label 'Dict Size:'
              }
              td {
                label id:'dictsize', text:''
              }
            }
          }
        }

        panel(constraints: BorderLayout.SOUTH) {
          tableLayout {
            tr {
              td {
                button text: 'Generate', actionPerformed: {
                  println new File('customdict.txt').delete()
                  Random ran = new Random()
                  if (specarea.text == '') {
                    specialfield.setBorder(BorderFactory.createLineBorder(colorlist[ran.nextInt(8)], 1))
                  } else {
                    possiblepwds(specarea.text)
                    def gend = generatepwd()
                    pwdlabel.text = gend[0]
                    dictsize.text = gend[1]
                    specarea.text = ''
                  }
                }
              }
            }
          }
        }
      }
}

def generatepwd() {
  def gen = ""
  Random random = new Random()

  File file = new File("customdict.txt")
  def possibilities = file.readLines()
  size = possibilities.size()

  def num = random.nextInt(possibilities.size())
  gen = possibilities[num]
  return [gen, size]
}

def possiblepwds(specs) {
  def words = specs.split('\n')
  def list = []

  words.each { word ->
    def templist = []
    templist.add(word.toLowerCase())
    templist.add(word.toUpperCase())
    templist.add(word.capitalize())
    list.add(templist)
  }

  def numrng = 0..99
  def comblist
  def other = ['@', '$', '&', '%', '/', '^',
              '*', '!', '(', ')', '-', '_', '+', '=']
  File f = new File('customdict.txt')
  comblist = list.combinations()
  comblist.each { innerlist ->
    list.add(innerlist.join())
  }

  list = list.flatten()
  list.each { word ->
    f << word
    other.each { o ->
      numrng.each { n ->
        n = n.toString()
        f << word + n + "\n"
        f << word + n + o + "\n"
        f << word + o + n + "\n"
      }
      f << word + o + "\n"
      f << word + o + o + "\n"
    }
  }
}
