import React, { useState } from 'react';
import AiGenerationTab from './components/AiGenerationTab';
import CsvUploadTab from './components/CsvUploadTab';
import { Tabs, TabsList, TabsTrigger, TabsContent } from './components/ui/tabs';

const App = () => {
  const [activeTab, setActiveTab] = useState('ai-generation');

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="w-full max-w-2xl mx-auto">
        <h1 className="text-3xl font-bold text-center mb-6">Kahoot Quiz Generator</h1>

        <Tabs value={activeTab} onValueChange={setActiveTab}>
          <TabsList className="grid w-full grid-cols-2 mb-6">
            <TabsTrigger value="ai-generation">AI Generation</TabsTrigger>
            <TabsTrigger value="csv-upload">CSV Upload</TabsTrigger>
          </TabsList>

          <TabsContent value="ai-generation">
            <AiGenerationTab />
          </TabsContent>

          <TabsContent value="csv-upload">
            <CsvUploadTab />
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};

export default App;
