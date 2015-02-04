require 'haml'

$: << File.dirname(__FILE__)

class Helper
end

class HamlRenderer
  def initialize(files, layout, options)
    @input  = files
    @ugly   = options[:ugly]
    @layout = Haml::Engine.new(layout, filename: "(layout)", ugly: @ugly)
  end

  def spit(file, contents)
    File.open(file, "w") do |f|
      f.write(contents)
    end
  end

  def extname(file)
    File.basename(file).gsub(/(\.html)?\.haml$/, ".html")
  end

  def outdir
    ENV['BOOT_TGT_PATH']
  end

  def run!
    @input.each do |input|
      f = File.new(input)
      source = f.read
      engine = Haml::Engine.new(source, {filename: File.basename(f), ugly: @ugly})
      target = File.join(outdir, extname(f))
      helper = Helper.new

      out = @layout.render(helper) do
        engine.render(helper)
      end

      spit(target, out)
    end
  end
end

# Global vars are set in boot-haml task
HamlRenderer.new($input, $layout, outdir: $outdir, ugly: $ugly).run!
