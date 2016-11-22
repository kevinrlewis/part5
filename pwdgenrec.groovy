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


//global file
f = new File('customdictrec.txt')

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
                  println new File('customdictrec.txt').delete()
                  if (specarea.text == '') {
                    Random ran = new Random()
                    specialfield.setBorder(BorderFactory.createLineBorder(colorlist[ran.nextInt(8)], 1))
                  } else {
                    ArrayList temp = specarea.text.split("\n")
                    possiblepwds(temp)
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

//pulls a password from the created dict file
def generatepwd() {
  def gen = ""
  def size = 0
  Random random = new Random()

  File file = new File("customdictrec.txt")
  def possibilities = file.readLines()
  size = possibilities.size()

  def num = random.nextInt(possibilities.size())
  gen = possibilities[num]
  return [gen, size]
}

//modify the words recursively
//creates all lowercase word
//creates all uppercase word
//create a capitalized word
def modifywords(words, list, i) {
  if(i < (words.size())) {
    list.add(words[i].toLowerCase())
    list.add(words[i].toUpperCase())
    list.add(words[i].capitalize())
    modifywords(words, list, i + 1)
  } else {
    return list
  }
}

//combines lists of multiple words
def joinlists(mainlist, list, i) {
  if(i < list.size()) {
    mainlist.add(list[i].join())
    joinlists(mainlist, list, i + 1)
  } else {
    return mainlist
  }
}

//recurses the lists then goes through a level
//of symbol recursion
def listrec(list, i) {
  def other = ['@', '$', '&', '%', '/', '^',
              '*', '!', '(', ')', '-', '_', '+', '=']
  if(i < list.size()) {
    f << list[i] + "\n"
    symrec(other, list[i], 0)
    listrec(list, i + 1)
  } else {
    return
  }
}

//recurses the symbols then goes through a level
//of number recursion
//writes to file
def symrec(other, word, i) {
  def numrng = 0..99
  if(i < other.size()) {
    f << word + other[i] + "\n"
    f << word + other[i] + other[i] + "\n"
    symrec(other, word, i + 1)
    numrec(numrng, word + other[i], 0)
  } else {
    numrec(numrng, word, 0)
    return
  }
}

//recurses the number possibilities
//writes to file
def numrec(num, word, i) {
  if(i < num.size()) {
    f << word + num[i] + "\n"
    numrec(num, word, i + 1)
  } else {
    return
  }
}

def possiblepwds(words) {
  def list = []
  def newlist = []
  def newnewlist = []
  list = modifywords(words, list, 0)

  def comblist
  def other = ['@', '$', '&', '%', '/', '^',
              '*', '!', '(', ')', '-', '_', '+', '=']


  comblist = [list, list].combinations()

  newlist = joinlists(list, comblist, 0)

  listrec(newlist, 0)

}
